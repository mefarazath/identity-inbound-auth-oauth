/*
 * Copyright (c) 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.oauth2.dao;

public class SQLQueries {

    public static final String STORE_AUTHORIZATION_CODE = "INSERT INTO  IDN_OAUTH2_AUTHORIZATION_CODE " +
            "(CODE_ID, AUTHORIZATION_CODE, CONSUMER_KEY_ID, CALLBACK_URL, SCOPE, AUTHZ_USER, USER_DOMAIN, TENANT_ID, " +
            "TIME_CREATED, VALIDITY_PERIOD, SUBJECT_IDENTIFIER) SELECT ?,?,ID,?,?,?,?,?,?,?,? FROM " +
            "IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY=?";
    public static final String STORE_AUTHORIZATION_CODE_WITH_PKCE = "INSERT INTO  IDN_OAUTH2_AUTHORIZATION_CODE " +
            "(CODE_ID, AUTHORIZATION_CODE, CONSUMER_KEY_ID, CALLBACK_URL, SCOPE, AUTHZ_USER, USER_DOMAIN, TENANT_ID, " +
            "TIME_CREATED, VALIDITY_PERIOD, SUBJECT_IDENTIFIER, PKCE_CODE_CHALLENGE, PKCE_CODE_CHALLENGE_METHOD) SELECT ?,?,ID,?,?,?,?,?,?,?,?,?,? FROM " +
            "IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY=?";

    public static final String VALIDATE_AUTHZ_CODE = "SELECT AUTHZ_USER, USER_DOMAIN, TENANT_ID, SCOPE, " +
            "CALLBACK_URL, TIME_CREATED,VALIDITY_PERIOD, STATE, TOKEN_ID, AUTHORIZATION_CODE, CODE_ID, SUBJECT_IDENTIFIER, " +
            "FROM IDN_OAUTH2_AUTHORIZATION_CODE WHERE " +
            "CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND AUTHORIZATION_CODE = ?";

    public static final String VALIDATE_AUTHZ_CODE_WITH_PKCE = "SELECT AUTHZ_USER, USER_DOMAIN, TENANT_ID, SCOPE, " +
            "CALLBACK_URL, TIME_CREATED,VALIDITY_PERIOD, STATE, TOKEN_ID, AUTHORIZATION_CODE, CODE_ID, SUBJECT_IDENTIFIER, " +
            "PKCE_CODE_CHALLENGE, PKCE_CODE_CHALLENGE_METHOD FROM IDN_OAUTH2_AUTHORIZATION_CODE WHERE " +
            "CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND AUTHORIZATION_CODE = ?";

    public static final String RETRIEVE_CODE_ID_BY_AUTHORIZATION_CODE = "SELECT CODE_ID FROM " +
            "IDN_OAUTH2_AUTHORIZATION_CODE WHERE AUTHORIZATION_CODE = ?";

    public static final String RETRIEVE_AUTHZ_CODE_BY_CODE_ID = "SELECT AUTHORIZATION_CODE FROM " +
            "IDN_OAUTH2_AUTHORIZATION_CODE WHERE CODE_ID = ?";

    public static final String RETRIEVE_TOKEN_ID_BY_TOKEN = "SELECT TOKEN_ID FROM " +
            "IDN_OAUTH2_ACCESS_TOKEN WHERE ACCESS_TOKEN = ?";

    public static final String RETRIEVE_TOKEN_BY_TOKEN_ID = "SELECT ACCESS_TOKEN FROM " +
            "IDN_OAUTH2_ACCESS_TOKEN WHERE TOKEN_ID = ?";

    public static final String UPDATE_TOKEN_AGAINST_AUTHZ_CODE = "UPDATE IDN_OAUTH2_AUTHORIZATION_CODE SET " +
                                                                 "TOKEN_ID=? WHERE AUTHORIZATION_CODE=?";

    public static final String GET_ACCESS_TOKEN_BY_AUTHZ_CODE = "SELECT AUTHORIZATION_CODE FROM " +
                                                                "IDN_OAUTH2_AUTHORIZATION_CODE WHERE " +
                                                                "TOKEN_ID=?";

    public static final String UPDATE_NEW_TOKEN_AGAINST_AUTHZ_CODE = "UPDATE IDN_OAUTH2_AUTHORIZATION_CODE SET " +
            "TOKEN_ID=? WHERE TOKEN_ID=?";

    public static final String DEACTIVATE_AUTHZ_CODE = "UPDATE IDN_OAUTH2_AUTHORIZATION_CODE SET " +
                                                   "STATE='INACTIVE' WHERE AUTHORIZATION_CODE= ?";

    public static final String EXPIRE_AUTHZ_CODE = "UPDATE IDN_OAUTH2_AUTHORIZATION_CODE SET " +
            "STATE='EXPIRED' WHERE AUTHORIZATION_CODE= ?";

    public static final String DEACTIVATE_AUTHZ_CODE_AND_INSERT_CURRENT_TOKEN = "UPDATE IDN_OAUTH2_AUTHORIZATION_CODE SET " +
                                                                           "STATE='INACTIVE', TOKEN_ID=?" +
                                                                            " WHERE AUTHORIZATION_CODE= ?";

    public static final String RETRIEVE_LATEST_ACCESS_TOKEN_BY_CLIENT_ID_USER_SCOPE_ORACLE = "SELECT * FROM (SELECT " +
            "ACCESS_TOKEN, REFRESH_TOKEN, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD,TOKEN_STATE, USER_TYPE, TOKEN_ID, SUBJECT_IDENTIFIER FROM IDN_OAUTH2_ACCESS_TOKEN WHERE " +
            "CONSUMER_KEY_ID=(SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND AUTHZ_USER=? AND " +
            "TENANT_ID=? AND USER_DOMAIN=? AND TOKEN_SCOPE_HASH=? ORDER BY TIME_CREATED DESC) WHERE ROWNUM < 2 ";

    public static final String RETRIEVE_LATEST_ACCESS_TOKEN_BY_CLIENT_ID_USER_SCOPE_MYSQL = "SELECT ACCESS_TOKEN, " +
            "REFRESH_TOKEN, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD," +
            " TOKEN_STATE, USER_TYPE, TOKEN_ID, SUBJECT_IDENTIFIER FROM IDN_OAUTH2_ACCESS_TOKEN WHERE CONSUMER_KEY_ID = (SELECT ID FROM " +
            "IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND AUTHZ_USER=? AND TENANT_ID=? AND USER_DOMAIN=? AND " +
            "TOKEN_SCOPE_HASH=? ORDER BY TIME_CREATED DESC LIMIT 1";

    public static final String RETRIEVE_LATEST_ACCESS_TOKEN_BY_CLIENT_ID_USER_SCOPE_DB2SQL = "SELECT ACCESS_TOKEN, " +
            "REFRESH_TOKEN, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD," +
            " TOKEN_STATE, USER_TYPE, TOKEN_ID, SUBJECT_IDENTIFIER FROM IDN_OAUTH2_ACCESS_TOKEN WHERE CONSUMER_KEY_ID= (SELECT ID FROM " +
            "IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND AUTHZ_USER=? AND TENANT_ID=? AND USER_DOMAIN=? AND" +
            " TOKEN_SCOPE_HASH=? ORDER BY TIME_CREATED DESC FETCH FIRST 1 ROWS ONLY";

    public static final String RETRIEVE_LATEST_ACCESS_TOKEN_BY_CLIENT_ID_USER_SCOPE_MSSQL = "SELECT TOP 1 " +
            "ACCESS_TOKEN, REFRESH_TOKEN, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_STATE, USER_TYPE, TOKEN_ID, SUBJECT_IDENTIFIER FROM IDN_OAUTH2_ACCESS_TOKEN WHERE " +
            "CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND AUTHZ_USER=? AND " +
            "TENANT_ID=? AND USER_DOMAIN=? AND TOKEN_SCOPE_HASH=? ORDER BY TIME_CREATED DESC";

    public static final String RETRIEVE_LATEST_ACCESS_TOKEN_BY_CLIENT_ID_USER_SCOPE_POSTGRESQL = "SELECT * FROM " +
            "(SELECT ACCESS_TOKEN, REFRESH_TOKEN, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_STATE, USER_TYPE, TOKEN_ID, SUBJECT_IDENTIFIER FROM IDN_OAUTH2_ACCESS_TOKEN WHERE " +
            "CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND AUTHZ_USER=? AND " +
            "TENANT_ID=? AND USER_DOMAIN=? AND TOKEN_SCOPE_HASH=? ORDER BY TIME_CREATED DESC) TOKEN LIMIT 1 ";

    public static final String RETRIEVE_LATEST_ACCESS_TOKEN_BY_CLIENT_ID_USER_SCOPE_INFORMIX = "SELECT FIRST 1 * FROM" +
            " (SELECT ACCESS_TOKEN, REFRESH_TOKEN, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_STATE, USER_TYPE, TOKEN_ID, SUBJECT_IDENTIFIER FROM IDN_OAUTH2_ACCESS_TOKEN WHERE " +
            "CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND AUTHZ_USER=? AND " +
            "TENANT_ID=? AND USER_DOMAIN=? AND TOKEN_SCOPE_HASH=? ORDER BY TIME_CREATED DESC) TOKEN ";

    public static final String RETRIEVE_ACTIVE_ACCESS_TOKEN_BY_CLIENT_ID_USER = "SELECT ACCESS_TOKEN, REFRESH_TOKEN, " +
            "TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, " +
            "TOKEN_SCOPE, ACCESS_TOKEN_TABLE.TOKEN_ID, SUBJECT_IDENTIFIER FROM (SELECT TOKEN_ID, ACCESS_TOKEN, REFRESH_TOKEN, " +
            "TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, SUBJECT_IDENTIFIER FROM" +
            " IDN_OAUTH2_ACCESS_TOKEN WHERE CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE " +
            "CONSUMER_KEY = ?) AND AUTHZ_USER=? AND TENANT_ID=? AND USER_DOMAIN=? AND TOKEN_STATE='ACTIVE') " +
            "ACCESS_TOKEN_TABLE LEFT JOIN IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON ACCESS_TOKEN_TABLE.TOKEN_ID = " +
            "IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String RETRIEVE_ACTIVE_EXPIRED_ACCESS_TOKEN_BY_CLIENT_ID_USER = "SELECT ACCESS_TOKEN, REFRESH_TOKEN, " +
            "TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, " +
            "TOKEN_SCOPE, ACCESS_TOKEN_TABLE.TOKEN_ID, SUBJECT_IDENTIFIER FROM (SELECT TOKEN_ID, ACCESS_TOKEN, REFRESH_TOKEN, " +
            "TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, SUBJECT_IDENTIFIER FROM" +
            " IDN_OAUTH2_ACCESS_TOKEN WHERE CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE " +
            "CONSUMER_KEY = ?) AND AUTHZ_USER=? AND TENANT_ID=? AND USER_DOMAIN=? AND (TOKEN_STATE='ACTIVE' OR " +
            "TOKEN_STATE='EXPIRED')) ACCESS_TOKEN_TABLE LEFT JOIN IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON ACCESS_TOKEN_TABLE" +
            ".TOKEN_ID = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String RETRIEVE_ACTIVE_ACCESS_TOKEN = "SELECT CONSUMER_KEY, AUTHZ_USER, ACCESS_TOKEN_TABLE" +
            ".TENANT_ID, USER_DOMAIN, TOKEN_SCOPE, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, REFRESH_TOKEN, ACCESS_TOKEN_TABLE.TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM (SELECT " +
            "TOKEN_ID, CONSUMER_KEY, AUTHZ_USER, IDN_OAUTH2_ACCESS_TOKEN.TENANT_ID, IDN_OAUTH2_ACCESS_TOKEN.USER_DOMAIN, TIME_CREATED, " +
            "REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, REFRESH_TOKEN, " +
            "IDN_OAUTH2_ACCESS_TOKEN.GRANT_TYPE, SUBJECT_IDENTIFIER FROM IDN_OAUTH2_ACCESS_TOKEN JOIN IDN_OAUTH_CONSUMER_APPS ON "
            + "CONSUMER_KEY_ID = ID " +
            "WHERE ACCESS_TOKEN=? AND TOKEN_STATE='ACTIVE') ACCESS_TOKEN_TABLE LEFT JOIN IDN_OAUTH2_ACCESS_TOKEN_SCOPE " +
            "ON ACCESS_TOKEN_TABLE.TOKEN_ID = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String RETRIEVE_ACTIVE_EXPIRED_ACCESS_TOKEN = "SELECT CONSUMER_KEY, AUTHZ_USER, " +
            "ACCESS_TOKEN_TABLE.TENANT_ID, USER_DOMAIN, TOKEN_SCOPE, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, " +
            "VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, REFRESH_TOKEN, ACCESS_TOKEN_TABLE.TOKEN_ID, " +
            "GRANT_TYPE, SUBJECT_IDENTIFIER " +
            "FROM (SELECT TOKEN_ID, CONSUMER_KEY, AUTHZ_USER, IDN_OAUTH2_ACCESS_TOKEN.TENANT_ID, " +
            "IDN_OAUTH2_ACCESS_TOKEN.USER_DOMAIN, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, REFRESH_TOKEN, IDN_OAUTH2_ACCESS_TOKEN.GRANT_TYPE, SUBJECT_IDENTIFIER " +
            "FROM IDN_OAUTH2_ACCESS_TOKEN JOIN IDN_OAUTH_CONSUMER_APPS ON CONSUMER_KEY_ID = ID " +
            "WHERE ACCESS_TOKEN=? AND (TOKEN_STATE='ACTIVE' OR TOKEN_STATE='EXPIRED')) ACCESS_TOKEN_TABLE LEFT " +
            "JOIN IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON ACCESS_TOKEN_TABLE.TOKEN_ID = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String UPDATE_TOKE_STATE = "UPDATE IDN_OAUTH2_ACCESS_TOKEN SET TOKEN_STATE=?, " +
            "TOKEN_STATE_ID=? WHERE TOKEN_ID=?";

    public static final String REVOKE_ACCESS_TOKEN_BY_TOKEN_ID = "UPDATE IDN_OAUTH2_ACCESS_TOKEN SET TOKEN_STATE=?, " +
            "TOKEN_STATE_ID=? WHERE TOKEN_ID=?";

    public static final String REVOKE_ACCESS_TOKEN = "UPDATE IDN_OAUTH2_ACCESS_TOKEN SET TOKEN_STATE=?, " +
                                                     "TOKEN_STATE_ID=? WHERE ACCESS_TOKEN=?";

    public static final String REVOKE_REFRESH_TOKEN = "UPDATE IDN_OAUTH2_ACCESS_TOKEN SET TOKEN_STATE=?, " +
            "TOKEN_STATE_ID=? WHERE REFRESH_TOKEN=?";

    public static final String GET_ACCESS_TOKEN_BY_AUTHZUSER = "SELECT DISTINCT ACCESS_TOKEN " +
            "FROM IDN_OAUTH2_ACCESS_TOKEN WHERE AUTHZ_USER=? AND TENANT_ID=? AND TOKEN_STATE=? AND USER_DOMAIN=?";

    public static final String GET_ACCESS_TOKENS_FOR_CONSUMER_KEY = "SELECT ACCESS_TOKEN FROM IDN_OAUTH2_ACCESS_TOKEN" +
            " WHERE CONSUMER_KEY_ID IN (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ? ) AND " +
            "TOKEN_STATE=?";

    public static final String GET_AUTHORIZATION_CODES_FOR_CONSUMER_KEY = "SELECT AUTHORIZATION_CODE   FROM " +
            "IDN_OAUTH2_AUTHORIZATION_CODE WHERE CONSUMER_KEY_ID IN (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE " +
            "CONSUMER_KEY = ?) ";

    public static final String GET_AUTHORIZATION_CODES_BY_AUTHZUSER = "SELECT DISTINCT AUTHORIZATION_CODE" +
            " FROM IDN_OAUTH2_AUTHORIZATION_CODE WHERE AUTHZ_USER=? AND TENANT_ID=? AND USER_DOMAIN=? AND STATE=?";

    public static final String GET_DISTINCT_APPS_AUTHORIZED_BY_USER_ALL_TIME = "SELECT DISTINCT CONSUMER_KEY FROM " +
            "IDN_OAUTH2_ACCESS_TOKEN JOIN IDN_OAUTH_CONSUMER_APPS ON CONSUMER_KEY_ID = " +
            "ID WHERE AUTHZ_USER=? AND IDN_OAUTH2_ACCESS_TOKEN.TENANT_ID=? AND IDN_OAUTH2_ACCESS_TOKEN.USER_DOMAIN=? " +
            "AND (TOKEN_STATE='ACTIVE' OR TOKEN_STATE='EXPIRED')";

    public static final String RETRIEVE_ACCESS_TOKEN_VALIDATION_DATA_MYSQL = "SELECT ACCESS_TOKEN, AUTHZ_USER, " +
            "ACCESS_TOKEN_SELECTED.TENANT_ID, USER_DOMAIN, TOKEN_SCOPE, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, ACCESS_TOKEN_SELECTED.TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM ( " +
            "SELECT ACCESS_TOKEN, AUTHZ_USER, TENANT_ID, USER_DOMAIN, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM $accessTokenStoreTable " +
            "WHERE CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND " +
            "REFRESH_TOKEN = ? ORDER BY TIME_CREATED DESC LIMIT 1) ACCESS_TOKEN_SELECTED LEFT JOIN " +
            "IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON ACCESS_TOKEN_SELECTED.TOKEN_ID = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String RETRIEVE_ACCESS_TOKEN_VALIDATION_DATA_DB2SQL = "SELECT ACCESS_TOKEN, AUTHZ_USER, " +
            "ACCESS_TOKEN_SELECTED.TENANT_ID, USER_DOMAIN, TOKEN_SCOPE, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, ACCESS_TOKEN_SELECTED.TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM ( " +
            "SELECT ACCESS_TOKEN, AUTHZ_USER, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED, REFRESH_TOKEN_VALIDITY_PERIOD," +
            " TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM $accessTokenStoreTable WHERE CONSUMER_KEY_ID = (SELECT ID" +
            " FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND REFRESH_TOKEN = ? ORDER BY TIME_CREATED DESC " +
            "FETCH FIRST 1 ROWS ONLY) ACCESS_TOKEN_SELECTED LEFT JOIN IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON " +
            "ACCESS_TOKEN_SELECTED.TOKEN_ID = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String RETRIEVE_ACCESS_TOKEN_VALIDATION_DATA_ORACLE = "SELECT ACCESS_TOKEN, AUTHZ_USER, " +
            "ACCESS_TOKEN_SELECTED.TENANT_ID, USER_DOMAIN, TOKEN_SCOPE, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, ACCESS_TOKEN_SELECTED.TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM ( " +
            "SELECT * FROM (SELECT ACCESS_TOKEN, AUTHZ_USER, TENANT_ID, USER_DOMAIN, TOKEN_STATE, " +
            "REFRESH_TOKEN_TIME_CREATED, REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM" +
            " $accessTokenStoreTable WHERE CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE " +
            "CONSUMER_KEY = ?) AND REFRESH_TOKEN = ? ORDER BY TIME_CREATED DESC) WHERE ROWNUM < 2 ) " +
            "ACCESS_TOKEN_SELECTED LEFT JOIN IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON ACCESS_TOKEN_SELECTED.TOKEN_ID = " +
            "IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String RETRIEVE_ACCESS_TOKEN_VALIDATION_DATA_MSSQL = "SELECT ACCESS_TOKEN, AUTHZ_USER, " +
            "ACCESS_TOKEN_SELECTED.TENANT_ID, USER_DOMAIN, TOKEN_SCOPE, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, ACCESS_TOKEN_SELECTED.TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM " +
            "(SELECT TOP 1 ACCESS_TOKEN, AUTHZ_USER, TENANT_ID, USER_DOMAIN, TOKEN_STATE, " +
            "REFRESH_TOKEN_TIME_CREATED, REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_ID, GRANT_TYPE, " +
            "SUBJECT_IDENTIFIER FROM $accessTokenStoreTable WHERE CONSUMER_KEY_ID = (SELECT ID FROM " +
            "IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND REFRESH_TOKEN = ? ORDER BY TIME_CREATED DESC) " +
            "ACCESS_TOKEN_SELECTED LEFT JOIN IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON ACCESS_TOKEN_SELECTED" +
            ".TOKEN_ID  = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String RETRIEVE_ACCESS_TOKEN_VALIDATION_DATA_POSTGRESQL = "SELECT ACCESS_TOKEN, AUTHZ_USER, " +
            "ACCESS_TOKEN_SELECTED.TENANT_ID, USER_DOMAIN, TOKEN_SCOPE, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED," +
            " REFRESH_TOKEN_VALIDITY_PERIOD, ACCESS_TOKEN_SELECTED.TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM " +
            "(SELECT ACCESS_TOKEN, AUTHZ_USER, TENANT_ID, USER_DOMAIN, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM $accessTokenStoreTable " +
            "WHERE CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND " +
            "REFRESH_TOKEN = ? ORDER BY TIME_CREATED DESC LIMIT 1) ACCESS_TOKEN_SELECTED LEFT JOIN " +
            "IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON ACCESS_TOKEN_SELECTED.TOKEN_ID = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String RETRIEVE_ACCESS_TOKEN_VALIDATION_DATA_INFORMIX = "SELECT ACCESS_TOKEN, AUTHZ_USER, " +
            "ACCESS_TOKEN_SELECTED.TENANT_ID, USER_DOMAIN, TOKEN_SCOPE, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, ACCESS_TOKEN_SELECTED.TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM ( " +
            "SELECT FIRST 1 ACCESS_TOKEN, AUTHZ_USER, TENANT_ID, USER_DOMAIN, TOKEN_STATE, REFRESH_TOKEN_TIME_CREATED, " +
            "REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER FROM $accessTokenStoreTable " +
            "WHERE CONSUMER_KEY_ID = (SELECT ID FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?) AND REFRESH_TOKEN = ? " +
            "ORDER BY TIME_CREATED DESC) ACCESS_TOKEN_SELECTED LEFT JOIN IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON " +
            "ACCESS_TOKEN_SELECTED.TOKEN_ID = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String INSERT_OAUTH2_ACCESS_TOKEN = "INSERT INTO $accessTokenStoreTable (ACCESS_TOKEN, " +
            "REFRESH_TOKEN, CONSUMER_KEY_ID, AUTHZ_USER, TENANT_ID, USER_DOMAIN, TIME_CREATED, " +
            "REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_SCOPE_HASH, " +
            "TOKEN_STATE, USER_TYPE, TOKEN_ID, GRANT_TYPE, SUBJECT_IDENTIFIER) SELECT ?,?,ID,?,?,?,?,?,?,?,?,?,?,?,?," +
            "? FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY=?";

    public static final String INSERT_OAUTH2_TOKEN_SCOPE = "INSERT INTO IDN_OAUTH2_ACCESS_TOKEN_SCOPE (TOKEN_ID, " +
            "TOKEN_SCOPE, TENANT_ID) VALUES (?,?,?)";

    public static final String DELETE_ACCESS_TOKEN = "DELETE FROM $accessTokenStoreTable WHERE ACCESS_TOKEN = ? ";

    public static final String RETRIEVE_IOS_SCOPE_KEY = "SELECT IOS.SCOPE_KEY FROM IDN_OAUTH2_SCOPE IOS, " +
            "IDN_OAUTH2_RESOURCE_SCOPE IORS WHERE RESOURCE_PATH = ? AND IORS.SCOPE_ID = IOS.SCOPE_ID";

    public static final String DELETE_IDN_OPENID_USER_RPS = "DELETE FROM IDN_OPENID_USER_RPS WHERE USER_NAME = ? AND " +
            "RP_URL = ?";

    public static final String RENAME_USER_STORE_IN_ACCESS_TOKENS_TABLE = "UPDATE IDN_OAUTH2_ACCESS_TOKEN SET " +
            "USER_DOMAIN=? WHERE TENANT_ID=? AND USER_DOMAIN=?";

    public static final String RENAME_USER_STORE_IN_AUTHORIZATION_CODES_TABLE = "UPDATE IDN_OAUTH2_AUTHORIZATION_CODE" +
            " SET USER_DOMAIN=? WHERE TENANT_ID=? AND USER_DOMAIN=?";

    public static final String LIST_ALL_TOKENS_IN_TENANT = "SELECT ACCESS_TOKEN, REFRESH_TOKEN, " +
            "TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, " +
            "TOKEN_SCOPE, ACCESS_TOKEN_TABLE.TOKEN_ID, AUTHZ_USER, USER_DOMAIN, CONSUMER_KEY FROM (SELECT AUTHZ_USER," +
            " USER_DOMAIN, CONSUMER_KEY_ID, TOKEN_ID, ACCESS_TOKEN, REFRESH_TOKEN, TIME_CREATED, " +
            "REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE FROM " +
            "IDN_OAUTH2_ACCESS_TOKEN WHERE TENANT_ID=? AND (TOKEN_STATE='ACTIVE' OR TOKEN_STATE='EXPIRED')) " +
            "ACCESS_TOKEN_TABLE JOIN IDN_OAUTH_CONSUMER_APPS ON ID = CONSUMER_KEY_ID LEFT JOIN " +
            "IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON ACCESS_TOKEN_TABLE.TOKEN_ID = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String LIST_ALL_TOKENS_IN_USER_STORE = "SELECT ACCESS_TOKEN, REFRESH_TOKEN, " +
            "TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE, " +
            "TOKEN_SCOPE, ACCESS_TOKEN_TABLE.TOKEN_ID, AUTHZ_USER, CONSUMER_KEY FROM (SELECT AUTHZ_USER, " +
            "CONSUMER_KEY_ID, TOKEN_ID, ACCESS_TOKEN, REFRESH_TOKEN, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, " +
            "VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, USER_TYPE FROM IDN_OAUTH2_ACCESS_TOKEN WHERE TENANT_ID=?" +
            " AND USER_DOMAIN=? AND (TOKEN_STATE='ACTIVE' OR TOKEN_STATE='EXPIRED')) ACCESS_TOKEN_TABLE JOIN " +
            "IDN_OAUTH_CONSUMER_APPS ON ID = CONSUMER_KEY_ID LEFT JOIN IDN_OAUTH2_ACCESS_TOKEN_SCOPE ON " +
            "ACCESS_TOKEN_TABLE.TOKEN_ID = IDN_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID";

    public static final String LIST_LATEST_AUTHZ_CODES_IN_USER_DOMAIN = "SELECT CODE_ID, AUTHORIZATION_CODE, " +
            "CONSUMER_KEY, IDN_OAUTH2_AUTHORIZATION_CODE.AUTHZ_USER, IDN_OAUTH2_AUTHORIZATION_CODE.SCOPE, " +
            "TIME_CREATED, VALIDITY_PERIOD, IDN_OAUTH2_AUTHORIZATION_CODE.CALLBACK_URL FROM (SELECT " +
            "AUTHZ_USER, CONSUMER_KEY_ID, SCOPE, MAX(TIME_CREATED) TIMES FROM IDN_OAUTH2_AUTHORIZATION_CODE WHERE " +
            "TENANT_ID=? AND USER_DOMAIN=? group by AUTHZ_USER, CONSUMER_KEY_ID, SCOPE) AUTHZ_SELECTED JOIN " +
            "IDN_OAUTH2_AUTHORIZATION_CODE ON AUTHZ_SELECTED.AUTHZ_USER=IDN_OAUTH2_AUTHORIZATION_CODE.AUTHZ_USER " +
            "AND AUTHZ_SELECTED.CONSUMER_KEY_ID=IDN_OAUTH2_AUTHORIZATION_CODE.CONSUMER_KEY_ID AND AUTHZ_SELECTED" +
            ".TIMES=IDN_OAUTH2_AUTHORIZATION_CODE.TIME_CREATED AND AUTHZ_SELECTED.SCOPE=IDN_OAUTH2_AUTHORIZATION_CODE" +
            ".SCOPE JOIN IDN_OAUTH_CONSUMER_APPS ON IDN_OAUTH2_AUTHORIZATION_CODE.CONSUMER_KEY_ID = ID WHERE " +
            "STATE='ACTIVE'";

    public static final String LIST_LATEST_AUTHZ_CODES_IN_TENANT = "SELECT CODE_ID, AUTHORIZATION_CODE, " +
            "CONSUMER_KEY, IDN_OAUTH2_AUTHORIZATION_CODE.AUTHZ_USER, IDN_OAUTH2_AUTHORIZATION_CODE.SCOPE, " +
            "TIME_CREATED, VALIDITY_PERIOD, IDN_OAUTH2_AUTHORIZATION_CODE.CALLBACK_URL, IDN_OAUTH2_AUTHORIZATION_CODE" +
            ".USER_DOMAIN FROM (SELECT AUTHZ_USER, USER_DOMAIN, CONSUMER_KEY_ID, SCOPE, MAX(TIME_CREATED) TIMES " +
            "FROM IDN_OAUTH2_AUTHORIZATION_CODE WHERE TENANT_ID=? group by AUTHZ_USER, " +
            "USER_DOMAIN, CONSUMER_KEY_ID, SCOPE) AUTHZ_SELECTED JOIN IDN_OAUTH2_AUTHORIZATION_CODE ON " +
            "AUTHZ_SELECTED.AUTHZ_USER=IDN_OAUTH2_AUTHORIZATION_CODE.AUTHZ_USER AND AUTHZ_SELECTED" +
            ".CONSUMER_KEY_ID=IDN_OAUTH2_AUTHORIZATION_CODE.CONSUMER_KEY_ID AND AUTHZ_SELECTED" +
            ".TIMES=IDN_OAUTH2_AUTHORIZATION_CODE.TIME_CREATED AND AUTHZ_SELECTED" +
            ".USER_DOMAIN=IDN_OAUTH2_AUTHORIZATION_CODE.USER_DOMAIN AND AUTHZ_SELECTED" +
            ".SCOPE=IDN_OAUTH2_AUTHORIZATION_CODE.SCOPE JOIN IDN_OAUTH_CONSUMER_APPS ON IDN_OAUTH2_AUTHORIZATION_CODE" +
            ".CONSUMER_KEY_ID = ID WHERE STATE='ACTIVE'";


    public static final String RETRIEVE_ROLES_OF_SCOPE = "SELECT IOS.ROLES FROM IDN_OAUTH2_SCOPE IOS WHERE SCOPE_KEY" +
                                                        " = ?";
    public static final String RETRIEVE_PKCE_TABLE_MYSQL = "SELECT PKCE_MANDATORY, PKCE_SUPPORT_PLAIN FROM " +
            "IDN_OAUTH_CONSUMER_APPS LIMIT 1";

    public static final String RETRIEVE_PKCE_TABLE_DB2SQL = "SELECT PKCE_MANDATORY, PKCE_SUPPORT_PLAIN FROM " +
            "IDN_OAUTH_CONSUMER_APPS FETCH FIRST 1 ROWS ONLY";

    public static final String RETRIEVE_PKCE_TABLE_MSSQL = "SELECT TOP 1 PKCE_MANDATORY, PKCE_SUPPORT_PLAIN FROM " +
            "IDN_OAUTH_CONSUMER_APPS";

    public static final String RETRIEVE_PKCE_TABLE_INFORMIX = "SELECT FIRST 1 PKCE_MANDATORY, PKCE_SUPPORT_PLAIN FROM " +
            "IDN_OAUTH_CONSUMER_APPS";

    public static final String RETRIEVE_PKCE_TABLE_ORACLE = "SELECT PKCE_MANDATORY, PKCE_SUPPORT_PLAIN FROM " +
            "IDN_OAUTH_CONSUMER_APPS WHERE ROWNUM < 2";

    private SQLQueries() {

    }
}
