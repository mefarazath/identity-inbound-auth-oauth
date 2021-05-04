/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth.dcr.service;

import org.apache.commons.lang.StringUtils;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.base.CarbonBaseConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.common.util.IdentityApplicationConstants;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;
import org.wso2.carbon.identity.application.mgt.ApplicationMgtUtil;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.oauth.IdentityOAuthAdminException;
import org.wso2.carbon.identity.oauth.OAuthAdminService;
import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth.dcr.DCRMConstants;
import org.wso2.carbon.identity.oauth.dcr.bean.Application;
import org.wso2.carbon.identity.oauth.dcr.bean.ApplicationRegistrationRequest;
import org.wso2.carbon.identity.oauth.dcr.bean.ApplicationUpdateRequest;
import org.wso2.carbon.identity.oauth.dcr.exception.DCRMException;
import org.wso2.carbon.identity.oauth.dcr.internal.DCRDataHolder;
import org.wso2.carbon.identity.oauth.dcr.util.DCRConstants;
import org.wso2.carbon.identity.oauth.dcr.util.ErrorCodes;
import org.wso2.carbon.identity.oauth.dto.OAuthConsumerAppDTO;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.idp.mgt.IdentityProviderManager;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.testng.Assert.assertEquals;
import static org.wso2.carbon.identity.oauth.common.OAuthConstants.OAuth10AParams.OAUTH_VERSION;

/**
 * Unit test covering DCRMService
 */
@PrepareForTest({DCRMService.class, ServiceProvider.class, IdentityProviderManager.class, ApplicationMgtUtil.class,
        OAuth2Util.class, OAuthServerConfiguration.class})
public class DCRMServiceTest extends PowerMockTestCase {

    private final String dummyConsumerKey = "dummyConsumerKey";
    private final String dummyClientName = "dummyClientName";
    private final String dummyInvalidClientName = "dummy@ClientName";
    private final List<String> dummyGrantTypes = new ArrayList<>();
    private final String dummyUserName = "dummyUserName";
    private final String dummyTenantDomain = "dummyTenantDomain";
    private final String dummyTokenType = "dummyTokenType";
    private String dummyConsumerSecret = "dummyConsumerSecret";
    private String dummyCallbackUrl = "dummyCallbackUrl";
    private final String dummyTemplateName = "dummyTemplateName";
    private final String dummyBackchannelLogoutUri = "http://backchannel.com/";

    @Mock
    private OAuthConsumerAppDTO dto;

    private DCRMService dcrmService;
    private OAuthAdminService mockOAuthAdminService;
    private ApplicationRegistrationRequest applicationRegistrationRequest;
    private ApplicationManagementService mockApplicationManagementService;
    private OAuthServerConfiguration oAuthServerConfiguration;
    private ApplicationUpdateRequest applicationUpdateRequest;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeMethod
    public void setUp() throws Exception {

        mockOAuthAdminService = mock(OAuthAdminService.class);
        applicationRegistrationRequest = new ApplicationRegistrationRequest();
        applicationRegistrationRequest.setClientName(dummyClientName);
        dcrmService = new DCRMService();
        mockApplicationManagementService = mock(ApplicationManagementService.class);
        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProviderByClientId(anyString(), anyString(), anyString()))
                .thenReturn(new ServiceProvider());
        mockStatic(ApplicationMgtUtil.class);
        when(ApplicationMgtUtil.isUserAuthorized(anyString(), anyString())).thenReturn(true);
        oAuthServerConfiguration = mock(OAuthServerConfiguration.class);
        mockStatic(OAuthServerConfiguration.class);
        when(OAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfiguration);
        mockStatic(OAuth2Util.class);
    }

    @DataProvider(name = "DTOProvider")
    public Object[][] getDTOStatus() {

        return new String[][]{
                {null},
                {""}
        };
    }

    @Test
    public void getApplicationEmptyClientIdTest() throws DCRMException {

        try {
            dcrmService.getApplication("");
        } catch (IdentityException ex) {
            assertEquals(ex.getMessage(), "Invalid client_id");
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    @Test(dataProvider = "DTOProvider")
    public void getApplicationNullDTOTest(String dtoStatus) throws Exception {

        if (dtoStatus == null) {
            when(mockOAuthAdminService.getOAuthApplicationData(dummyConsumerKey)).thenReturn(null);
            when(mockOAuthAdminService.getAllOAuthApplicationData()).thenReturn(new OAuthConsumerAppDTO[0]);
        } else {
            OAuthConsumerAppDTO dto = new OAuthConsumerAppDTO();
            dto.setApplicationName("");
            when(mockOAuthAdminService.getOAuthApplicationData(dummyConsumerKey)).thenReturn(dto);
            when(mockOAuthAdminService.getAllOAuthApplicationData()).thenReturn(new OAuthConsumerAppDTO[]{dto});
        }
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        try {
            dcrmService.getApplication(dummyConsumerKey);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.NOT_FOUND_APPLICATION_WITH_ID.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    @Test
    public void getApplicationDTOTestWithIOAException() throws Exception {

        doThrow(new IdentityOAuthAdminException("")).when(mockOAuthAdminService)
                .getOAuthApplicationData(dummyConsumerKey);
        when(mockOAuthAdminService.getAllOAuthApplicationData()).thenReturn(new OAuthConsumerAppDTO[0]);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        try {
            dcrmService.getApplication(dummyConsumerKey);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FAILED_TO_GET_APPLICATION_BY_ID.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    @Test
    public void getApplicationDTOTestWithIOCException() throws Exception {

        doThrow(new IdentityOAuthAdminException("", new InvalidOAuthClientException(""))).when(mockOAuthAdminService)
                .getOAuthApplicationData(dummyConsumerKey);
        when(mockOAuthAdminService.getAllOAuthApplicationData()).thenReturn(new OAuthConsumerAppDTO[0]);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        try {
            dcrmService.getApplication(dummyConsumerKey);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.NOT_FOUND_APPLICATION_WITH_ID.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    @Test
    public void getApplicationDTOTestUserUnauthorized() throws Exception {

        startTenantFlow();
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);
        when(ApplicationMgtUtil.isUserAuthorized(anyString(), anyString())).thenReturn(false);
        when(mockOAuthAdminService.getOAuthApplicationData(dummyConsumerKey)).thenReturn(dto);
        when(dto.getApplicationName()).thenReturn(dummyClientName);

        try {
            dcrmService.getApplication(dummyConsumerKey);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FORBIDDEN_UNAUTHORIZED_USER.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    @Test
    public void getApplicationDTOTest() throws Exception {

        startTenantFlow();
        OAuthConsumerAppDTO dto = new OAuthConsumerAppDTO();
        dto.setApplicationName(dummyClientName);
        String dummyConsumerSecret = "dummyConsumerSecret";
        dto.setOauthConsumerSecret(dummyConsumerSecret);
        dto.setOauthConsumerKey(dummyConsumerKey);
        String dummyCallbackUrl = "dummyCallbackUrl";
        dto.setCallbackUrl(dummyCallbackUrl);
        dto.setUsername(dummyUserName.concat("@").concat(dummyTenantDomain));

        when(mockOAuthAdminService.getOAuthApplicationData(dummyConsumerKey)).thenReturn(dto);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);
        Application application = dcrmService.getApplication(dummyConsumerKey);

        assertEquals(application.getClientId(), dummyConsumerKey);
        assertEquals(application.getClientName(), dummyClientName);
        assertEquals(application.getClientSecret(), dummyConsumerSecret);
        assertEquals(application.getRedirectUris().get(0), dummyCallbackUrl);
    }

    @Test
    public void getApplicationByNameTest() throws Exception {

        startTenantFlow();
        OAuthConsumerAppDTO oAuthConsumerApp = new OAuthConsumerAppDTO();
        oAuthConsumerApp.setApplicationName(dummyClientName);
        String dummyConsumerSecret = "dummyConsumerSecret";
        oAuthConsumerApp.setOauthConsumerSecret(dummyConsumerSecret);
        oAuthConsumerApp.setOauthConsumerKey(dummyConsumerKey);
        String dummyCallbackUrl = "dummyCallbackUrl";
        oAuthConsumerApp.setCallbackUrl(dummyCallbackUrl);
        oAuthConsumerApp.setUsername(dummyUserName.concat("@").concat(dummyTenantDomain));

        when(mockApplicationManagementService.getServiceProvider(anyString(), anyString()))
                .thenReturn(new ServiceProvider());
        when(mockOAuthAdminService
                .getOAuthApplicationDataByAppName(dummyClientName)).thenReturn(oAuthConsumerApp);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);
        Application application = dcrmService.getApplicationByName(dummyClientName);

        assertEquals(application.getClientId(), dummyConsumerKey);
        assertEquals(application.getClientName(), dummyClientName);
        assertEquals(application.getClientSecret(), dummyConsumerSecret);
        assertEquals(application.getRedirectUris().get(0), dummyCallbackUrl);
    }

    @Test
    public void getApplicationEmptyClientNameTest() throws DCRMException {

        try {
            dcrmService.getApplicationByName("");
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.BAD_REQUEST_INSUFFICIENT_DATA.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    @Test
    public void getApplicationNullNameTest() throws Exception {

        startTenantFlow();
        try {
            dcrmService.getApplicationByName(dummyClientName);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.NOT_FOUND_APPLICATION_WITH_NAME.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    @Test
    public void getApplicationNameTestWithIOCExceptionTest() throws Exception {

        startTenantFlow();
        doThrow(new IdentityOAuthAdminException("", new InvalidOAuthClientException(""))).when(mockOAuthAdminService)
                .getOAuthApplicationDataByAppName(dummyClientName);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        when(mockApplicationManagementService.getServiceProvider(anyString(), anyString()))
                .thenReturn(new ServiceProvider());
        try {
            dcrmService.getApplicationByName(dummyClientName);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FAILED_TO_GET_APPLICATION.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    @Test
    public void getApplicationByNameUserUnauthorizedTest() throws Exception {

        startTenantFlow();
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        when(mockApplicationManagementService.getServiceProvider(anyString(), anyString()))
                .thenReturn(new ServiceProvider());
        when(mockOAuthAdminService.getOAuthApplicationDataByAppName(dummyClientName))
                .thenReturn(new OAuthConsumerAppDTO());
        when(ApplicationMgtUtil.isUserAuthorized(anyString(), anyString())).thenReturn(false);
        try {
            dcrmService.getApplicationByName(dummyClientName);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FORBIDDEN_UNAUTHORIZED_USER.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    @Test
    public void registerApplicationTestWithExistSP() throws DCRMException, IdentityApplicationManagementException {

        dummyGrantTypes.add("dummy1");
        dummyGrantTypes.add("dummy2");
        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);

        startTenantFlow();
        mockApplicationManagementService = mock(ApplicationManagementService.class);
        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProvider(dummyClientName, dummyTenantDomain)).thenReturn(new
                ServiceProvider());

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.CONFLICT_EXISTING_APPLICATION.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test
    public void registerApplicationTestWithFailedToGetSP() throws DCRMException,
            IdentityApplicationManagementException {

        dummyGrantTypes.add("dummy1");
        dummyGrantTypes.add("dummy2");

        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);

        startTenantFlow();

        mockApplicationManagementService = mock(ApplicationManagementService.class);
        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);

        doThrow(new IdentityApplicationManagementException("")).when(mockApplicationManagementService)
                .getServiceProvider(dummyClientName, dummyTenantDomain);

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FAILED_TO_GET_SP.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test
    public void registerApplicationTestWithFailedToRegisterSP() throws Exception {

        dummyGrantTypes.add("dummy1");
        dummyGrantTypes.add("dummy2");
        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);

        startTenantFlow();

        mockApplicationManagementService = mock(ApplicationManagementService.class);
        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FAILED_TO_REGISTER_SP.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test
    public void registerApplicationTestWithExistClientId() throws Exception {

        dummyGrantTypes.add("dummy1");
        dummyGrantTypes.add("dummy2");

        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);
        applicationRegistrationRequest.setConsumerKey(dummyConsumerKey);
        startTenantFlow();
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        when(mockOAuthAdminService.getOAuthApplicationData(dummyConsumerKey))
                .thenReturn(dto);
        when(dto.getApplicationName()).thenReturn(dummyClientName);

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.CONFLICT_EXISTING_CLIENT_ID.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @DataProvider(name = "RedirectAndGrantTypeProvider")
    public Object[][] getListSizeAndGrantType() {

        List<String> redirectUri1 = new ArrayList<>();
        return new Object[][]{
                {DCRConstants.GrantTypes.IMPLICIT, redirectUri1},
                {DCRConstants.GrantTypes.AUTHORIZATION_CODE, redirectUri1},
        };
    }

    @Test(dataProvider = "RedirectAndGrantTypeProvider")
    public void registerApplicationTestWithSPWithFailCallback(String grantTypeVal, List<String> redirectUri)
            throws Exception {

        mockApplicationManagementService = mock(ApplicationManagementService.class);

        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        startTenantFlow();

        dummyGrantTypes.add(grantTypeVal);
        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);

        String grantType = StringUtils.join(applicationRegistrationRequest.getGrantTypes(), " ");

        ServiceProvider serviceProvider = new ServiceProvider();

        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProvider(dummyClientName, dummyTenantDomain)).thenReturn
                (null, serviceProvider);

        applicationRegistrationRequest.setRedirectUris(redirectUri);

        OAuthConsumerAppDTO oAuthConsumerApp = new OAuthConsumerAppDTO();
        oAuthConsumerApp.setApplicationName(dummyClientName);

        oAuthConsumerApp.setGrantTypes(grantType);
        oAuthConsumerApp.setOAuthVersion(OAUTH_VERSION);

        when(mockOAuthAdminService
                .getOAuthApplicationDataByAppName(dummyClientName)).thenReturn(oAuthConsumerApp);

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.BAD_REQUEST_INVALID_INPUT.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @DataProvider(name = "redirectUriProvider")
    public Object[][] getReDirecturi() {

        List<String> redirectUri1 = new ArrayList<>();
        redirectUri1.add("redirectUri1");
        List<String> redirectUri2 = new ArrayList<>();
        redirectUri2.add("redirectUri1");
        redirectUri2.add("redirectUri1");
        return new Object[][]{
                {redirectUri1},
                {redirectUri2}
        };
    }

    @Test(dataProvider = "redirectUriProvider")
    public void registerApplicationTestWithSP(List<String> redirectUri) throws Exception {

        mockApplicationManagementService = mock(ApplicationManagementService.class);

        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        startTenantFlow();

        dummyGrantTypes.add("implicit");
        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);
        applicationRegistrationRequest.setConsumerSecret(dummyConsumerSecret);
        applicationRegistrationRequest.setTokenType(dummyTokenType);
        applicationRegistrationRequest.setBackchannelLogoutUri(dummyBackchannelLogoutUri);
        applicationRegistrationRequest.setConsumerKey(dummyConsumerKey);

        String grantType = StringUtils.join(applicationRegistrationRequest.getGrantTypes(), " ");

        ServiceProvider serviceProvider = new ServiceProvider();

        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProvider(dummyClientName, dummyTenantDomain)).thenReturn
                (null, serviceProvider);

        applicationRegistrationRequest.setRedirectUris(redirectUri);

        OAuthConsumerAppDTO oAuthConsumerApp = new OAuthConsumerAppDTO();
        oAuthConsumerApp.setApplicationName(dummyClientName);
        oAuthConsumerApp.setGrantTypes(dummyGrantTypes.get(0));
        oAuthConsumerApp.setOauthConsumerKey(dummyConsumerKey);
        oAuthConsumerApp.setOauthConsumerSecret(dummyConsumerSecret);
        oAuthConsumerApp.setCallbackUrl(redirectUri.get(0));
        oAuthConsumerApp.setGrantTypes(grantType);
        oAuthConsumerApp.setOAuthVersion(OAUTH_VERSION);

        when(mockOAuthAdminService
                .getOAuthApplicationDataByAppName(dummyClientName)).thenReturn(oAuthConsumerApp);
        when(mockOAuthAdminService.registerAndRetrieveOAuthApplicationData(any(OAuthConsumerAppDTO.class)))
                .thenReturn(oAuthConsumerApp);
        OAuthServerConfiguration oAuthServerConfiguration = OAuthServerConfiguration.getInstance();
        assertNotNull(oAuthServerConfiguration);
        when(oAuthServerConfiguration.getClientIdValidationRegex()).thenReturn("[a-zA-Z0-9_]{15,30}");
        String toString =  "Application {\n" +
                "  clientName: " + oAuthConsumerApp.getApplicationName() + "\n" +
                "  clientId: " + oAuthConsumerApp.getOauthConsumerKey() + "\n" +
                "  clientSecret: " + oAuthConsumerApp.getOauthConsumerSecret() + "\n" +
                "  redirectUris: " +  Arrays.asList(oAuthConsumerApp.getCallbackUrl()) + "\n" +
                "  grantTypes: " + Arrays.asList(oAuthConsumerApp.getGrantTypes().split(" ")) + "\n" +
                "}\n";
        Application application = dcrmService.registerApplication(applicationRegistrationRequest);
        assertEquals(application.getClientName(), dummyClientName);
        assertEquals(application.getGrantTypes(), dummyGrantTypes);
        assertEquals(application.toString(), toString);

    }

    @Test
    public void testRegisterApplicationWithInvalidSPName() throws Exception {

        mockApplicationManagementService = mock(ApplicationManagementService.class);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);
        startTenantFlow();

        dummyGrantTypes.add("implicit");
        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);
        applicationRegistrationRequest.setClientName(dummyInvalidClientName);

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.BAD_REQUEST_INVALID_SP_NAME.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test(dataProvider = "redirectUriProvider")
    public void registerApplicationTestWithDeleteCreatedSP(List<String> redirectUri) throws Exception {

        mockStatic(IdentityProviderManager.class);

        mockApplicationManagementService = mock(ApplicationManagementService.class);

        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        startTenantFlow();

        dummyGrantTypes.add("implicit");
        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);

        String grantType = StringUtils.join(applicationRegistrationRequest.getGrantTypes(), " ");

        ServiceProvider serviceProvider = new ServiceProvider();

        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProvider(dummyClientName, dummyTenantDomain)).thenReturn
                (null, serviceProvider);

        applicationRegistrationRequest.setRedirectUris(redirectUri);

        OAuthConsumerAppDTO oAuthConsumerApp = new OAuthConsumerAppDTO();
        oAuthConsumerApp.setApplicationName(dummyClientName);

        oAuthConsumerApp.setGrantTypes(grantType);
        oAuthConsumerApp.setOAuthVersion(OAUTH_VERSION);

        whenNew(OAuthConsumerAppDTO.class).withNoArguments().thenReturn(oAuthConsumerApp);

        doThrow(new IdentityOAuthAdminException("")).when(mockOAuthAdminService)
                .registerOAuthApplicationData(oAuthConsumerApp);

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FAILED_TO_REGISTER_APPLICATION.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test(dataProvider = "redirectUriProvider")
    public void registerApplicationTestWithFailedToDeleteCreatedSP(List<String> redirectUri) throws Exception {

        mockStatic(IdentityProviderManager.class);
        mockApplicationManagementService = mock(ApplicationManagementService.class);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);

        startTenantFlow();

        dummyGrantTypes.add(DCRConstants.GrantTypes.IMPLICIT);
        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);

        String grantType = StringUtils.join(applicationRegistrationRequest.getGrantTypes(), " ");

        ServiceProvider serviceProvider = new ServiceProvider();

        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProvider(dummyClientName, dummyTenantDomain)).thenReturn
                (null, serviceProvider);

        applicationRegistrationRequest.setRedirectUris(redirectUri);

        OAuthConsumerAppDTO oAuthConsumerApp = new OAuthConsumerAppDTO();
        oAuthConsumerApp.setApplicationName(dummyClientName);

        oAuthConsumerApp.setGrantTypes(grantType);
        oAuthConsumerApp.setOAuthVersion(OAUTH_VERSION);

        whenNew(OAuthConsumerAppDTO.class).withNoArguments().thenReturn(oAuthConsumerApp);

        doThrow(new IdentityOAuthAdminException("")).when(mockOAuthAdminService)
                .registerOAuthApplicationData(oAuthConsumerApp);
        doThrow(new IdentityApplicationManagementException("")).when(mockApplicationManagementService)
                .deleteApplication(dummyClientName, dummyTenantDomain, dummyUserName);

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FAILED_TO_DELETE_SP.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test(dataProvider = "redirectUriProvider")
    public void registerApplicationTestWithFailedToUpdateSPTest(List<String> redirectUri) throws Exception {

        registerApplicationTestWithFailedToUpdateSP();
        applicationRegistrationRequest.setRedirectUris(redirectUri);

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FAILED_TO_UPDATE_SP.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test(dataProvider = "redirectUriProvider")
    public void registerApplicationTestWithInvalidSpTemplateNameTest(List<String> redirectUri) throws Exception {

        registerApplicationTestWithFailedToUpdateSP();

        applicationRegistrationRequest.setRedirectUris(redirectUri);
        applicationRegistrationRequest.setSpTemplateName("");

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(),
                    DCRMConstants.ErrorMessages.BAD_REQUEST_INVALID_SP_TEMPLATE_NAME.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test(dataProvider = "redirectUriProvider")
    public void registerApplicationTestWithErrorCreataingSPTenantTest(List<String> redirectUri) throws Exception {

        mockApplicationManagementService = mock(ApplicationManagementService.class);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);
        startTenantFlow();

        dummyGrantTypes.add("implicit");
        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);

        ServiceProvider serviceProvider = new ServiceProvider();
        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProvider(dummyClientName, dummyTenantDomain))
                .thenReturn(null, serviceProvider);
        applicationRegistrationRequest.setRedirectUris(redirectUri);
        applicationRegistrationRequest.setSpTemplateName(dummyTemplateName);
        whenNew(ServiceProvider.class).withNoArguments().thenReturn
                (serviceProvider);
        when(mockApplicationManagementService.isExistingApplicationTemplate(dummyTemplateName, dummyTenantDomain))
                .thenReturn(true);
        doThrow(new IdentityApplicationManagementException("")).when(mockApplicationManagementService)
                .createApplicationWithTemplate(serviceProvider, dummyTenantDomain, dummyUserName, dummyTemplateName);

        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), ErrorCodes.BAD_REQUEST.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test(dataProvider = "redirectUriProvider")
    public void deleteOAuthApplicationWithoutAssociatedSPwithError(List<String> redirectUri) throws Exception {

        OAuthConsumerAppDTO oAuthConsumerApp = registerApplicationTestWithFailedToUpdateSP();
        applicationRegistrationRequest.setRedirectUris(redirectUri);
        doThrow(new IdentityOAuthAdminException("")).when(mockOAuthAdminService)
                .removeOAuthApplicationData(oAuthConsumerApp.getOauthConsumerKey());
        try {
            dcrmService.registerApplication(applicationRegistrationRequest);
        } catch (IdentityException ex) {
            assertEquals(ex.getMessage(), "Error while deleting the OAuth application with consumer key: " +
                    oAuthConsumerApp.getOauthConsumerKey());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    private OAuthConsumerAppDTO registerApplicationTestWithFailedToUpdateSP() throws Exception {

        mockApplicationManagementService = mock(ApplicationManagementService.class);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);
        startTenantFlow();

        dummyGrantTypes.add("implicit");
        applicationRegistrationRequest.setGrantTypes(dummyGrantTypes);
        String grantType = StringUtils.join(applicationRegistrationRequest.getGrantTypes(), " ");

        ServiceProvider serviceProvider = new ServiceProvider();
        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProvider(dummyClientName, dummyTenantDomain)).thenReturn
                (null, serviceProvider);

        OAuthConsumerAppDTO oAuthConsumerApp = new OAuthConsumerAppDTO();
        oAuthConsumerApp.setApplicationName(dummyClientName);

        oAuthConsumerApp.setGrantTypes(grantType);
        oAuthConsumerApp.setOAuthVersion(OAUTH_VERSION);
        oAuthConsumerApp.setOauthConsumerKey("dummyConsumerKey");
        oAuthConsumerApp.setUsername(dummyUserName.concat("@").concat(dummyTenantDomain));

        when(mockOAuthAdminService
                .getOAuthApplicationDataByAppName(dummyClientName)).thenReturn(oAuthConsumerApp);
        when(mockOAuthAdminService
                .getOAuthApplicationData("dummyConsumerKey")).thenReturn(oAuthConsumerApp);
        when(mockOAuthAdminService.getAllOAuthApplicationData())
                .thenReturn(new OAuthConsumerAppDTO[]{oAuthConsumerApp});
        when(mockOAuthAdminService.registerAndRetrieveOAuthApplicationData(any(OAuthConsumerAppDTO.class))).
                thenReturn(oAuthConsumerApp);

        doThrow(new IdentityApplicationManagementException("ehweh")).when(mockApplicationManagementService)
                .updateApplication(serviceProvider, dummyTenantDomain, dummyUserName);


        when(mockApplicationManagementService.
                getServiceProviderNameByClientId(oAuthConsumerApp.getOauthConsumerKey(),
                        DCRMConstants.OAUTH2, dummyTenantDomain))
                .thenReturn(IdentityApplicationConstants.DEFAULT_SP_CONFIG);
        return oAuthConsumerApp;
    }

    @Test(dataProvider = "redirectUriProvider")
    public void updateApplicationTest(List<String> redirectUri1) throws Exception {

        updateApplication();
        applicationUpdateRequest.setRedirectUris(redirectUri1);
        Application application = dcrmService.updateApplication(applicationUpdateRequest, dummyConsumerKey);

        assertEquals(application.getClientId(), dummyConsumerKey);
        assertEquals(application.getClientName(), dummyClientName);
        assertEquals(application.getClientSecret(), dummyConsumerSecret);

    }

    @Test
    public void updateApplicationTestWithFailedToGetSP() throws Exception {

        updateApplication();
        when(mockApplicationManagementService.getServiceProvider(dummyClientName, dummyTenantDomain))
                .thenReturn(null);

        try {
            dcrmService.updateApplication(applicationUpdateRequest, dummyConsumerKey);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FAILED_TO_GET_SP.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }


    @Test
    public void updateApplicationTestWithInvalidSPName() throws Exception {

        updateApplication();
        applicationUpdateRequest.setClientName(dummyInvalidClientName);

        try {
            dcrmService.updateApplication(applicationUpdateRequest, dummyConsumerKey);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.BAD_REQUEST_INVALID_SP_NAME.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test
    public void updateApplicationTestWithSPAlreadyExist() throws Exception {

        startTenantFlow();
        updateApplication();
        applicationUpdateRequest.setClientName("dummynewClientName");

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setApplicationName(dummyClientName);

        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProvider("dummynewClientName", dummyTenantDomain))
                .thenReturn(serviceProvider);

        try {
            dcrmService.updateApplication(applicationUpdateRequest, dummyConsumerKey);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.CONFLICT_EXISTING_APPLICATION.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by registerApplication method");
    }

    @Test
    public void updateApplicationTestWithIOAException() throws Exception {

        dto = updateApplication();
        doThrow(new IdentityOAuthAdminException("")).when(mockOAuthAdminService)
                .updateConsumerApplication(dto);
        try {
            dcrmService.updateApplication(applicationUpdateRequest, dummyConsumerKey);
        } catch (IdentityException ex) {
            assertEquals(ex.getErrorCode(), DCRMConstants.ErrorMessages.FAILED_TO_UPDATE_APPLICATION.toString());
            return;
        }
        fail("Expected exception IdentityException not thrown by getApplication method");
    }

    private OAuthConsumerAppDTO updateApplication()
            throws IdentityOAuthAdminException, IdentityApplicationManagementException {

        startTenantFlow();
        dummyGrantTypes.add("dummy1");
        dummyGrantTypes.add("dummy2");
        applicationUpdateRequest = new ApplicationUpdateRequest();
        applicationUpdateRequest.setClientName(dummyClientName);
        applicationUpdateRequest.setGrantTypes(dummyGrantTypes);
        applicationUpdateRequest.setTokenType(dummyTokenType);
        applicationUpdateRequest.setBackchannelLogoutUri(dummyBackchannelLogoutUri);

        OAuthConsumerAppDTO dto = new OAuthConsumerAppDTO();
        dto.setApplicationName(dummyClientName);
        dto.setOauthConsumerSecret(dummyConsumerSecret);
        dto.setOauthConsumerKey(dummyConsumerKey);
        dto.setCallbackUrl(dummyCallbackUrl);
        dto.setUsername(dummyUserName.concat("@").concat(dummyTenantDomain));

        when(mockOAuthAdminService.getOAuthApplicationData(dummyConsumerKey)).thenReturn(dto);
        Whitebox.setInternalState(dcrmService, "oAuthAdminService", mockOAuthAdminService);
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setApplicationName(dummyClientName);

        DCRDataHolder dcrDataHolder = DCRDataHolder.getInstance();
        dcrDataHolder.setApplicationManagementService(mockApplicationManagementService);
        when(mockApplicationManagementService.getServiceProvider(dummyClientName, dummyTenantDomain))
                .thenReturn(serviceProvider);
        return dto;
    }

    private void startTenantFlow() {

        String carbonHome = Paths.get(System.getProperty("user.dir"), "src", "test", "resources").toString();
        System.setProperty(CarbonBaseConstants.CARBON_HOME, carbonHome);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(dummyTenantDomain);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(dummyUserName);
    }

}
