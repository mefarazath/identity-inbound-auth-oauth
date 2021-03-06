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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.identity.oidc.session.backchannellogout;

import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Logout token generator for the OpenID Connect back-channel logout Implementation.
 */
public interface LogoutTokenBuilder {

    /**
     * Returns logout token and back-channel logout uri map.
     *
     * @param request HttpServletRequest
     * @return a map of logout tokens and corresponding back-channel logout URLs.
     * @throws IdentityOAuth2Exception
     */
    Map<String, String> buildLogoutToken(HttpServletRequest request)
            throws IdentityOAuth2Exception, InvalidOAuthClientException;

    /**
     * Returns logout token and back-channel logout uri map.
     *
     * @param opbsCookie Opbscookie value.
     * @return A map of logout tokens and corresponding back-channel logout URLs.
     * @throws IdentityOAuth2Exception
     */
    Map<String, String> buildLogoutToken(String opbsCookie)
            throws IdentityOAuth2Exception, InvalidOAuthClientException;
}
