package com.jaspersoft.jasperserver.ps.OAuth;
/* Copyright 2014 Ronald Meadows
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.TenantImpl;
import com.jaspersoft.jasperserver.multipleTenancy.MTUserDetails;

public class OAuthUserDetailsServiceImpl implements OAuthUserDetailsService {
    private static Log log = LogFactory.getLog(OAuthUserDetailsServiceImpl.class);

    @Override
    public UserDetails parseUserDetails(String jsonResponse) {
         if (jsonResponse != null) {
            JSONObject myclaims = null;
            JSONObject myprincipal = null;
            String state = null;
            String roles = null;
            String tenantname = null;
            String tenantid = null;
            String username = null;
            String displayname = null;
            List<OAuthAuthorityImpl> myauthorities = null;
            boolean isActive = true;

            try {
                myclaims = new JSONObject(jsonResponse);
                username = myclaims.getString("name");
                displayname = myclaims.getString("name");
                if(myclaims.has("roles")) {
                    roles = myclaims.getString("roles");
                }

                if(myclaims.has("organization")) {
                    tenantname = myclaims.getString("organization");
                    tenantid = myclaims.getString("organization");
                } else {
                    tenantname = "Organization";
                    tenantid = "organization_1";
                }


                isActive = true;
                state = "";
                myauthorities = new ArrayList<OAuthAuthorityImpl>();


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            //create tenant structure with state at the top and then a child of district
            //checks for parent and creates if dne and then does the same for child
            if (username != null && tenantname != null && tenantid != null && state != null) {
                OAuthTenantInfo myt = new OAuthTenantInfo(tenantid, tenantname, tenantname);
                List<MTUserDetails.TenantInfo> mytenants = new Vector<MTUserDetails.TenantInfo>();
                mytenants.add(myt);
                return createUserDetails(myauthorities, username, displayname, "4N3v3R6u3s5", tenantid, mytenants, username, isActive);
            }
        } else {
            log.error("username not available in claims information");
            return null;
        }


        log.error("json response was null");
        return null;
    }


    private UserDetails createUserDetails(Collection<? extends GrantedAuthority> grantedAuthorities,
                                          String username, String fullname, String pw, String orgId,
                                          List<MTUserDetails.TenantInfo> mytenants, String email, boolean isActive) {



        OAuthMTUserDetails wrappingUser = new OAuthMTUserDetails(grantedAuthorities, username, mytenants);
        wrappingUser.setUsername(username);
        wrappingUser.setPassword(pw);
        wrappingUser.setAccountNonExpired(true);
        wrappingUser.setAccountNonLocked(true);
        wrappingUser.setAuthorities(grantedAuthorities);
        wrappingUser.setCredentialsNonExpired(true);
        wrappingUser.setEnabled(isActive);
        wrappingUser.setEmailAddress(email);
        wrappingUser.setFullName(fullname);
        // check during testing
        wrappingUser.setExternallyDefined(true);
        wrappingUser.setTenantId(orgId);
        return wrappingUser;
    }
}
