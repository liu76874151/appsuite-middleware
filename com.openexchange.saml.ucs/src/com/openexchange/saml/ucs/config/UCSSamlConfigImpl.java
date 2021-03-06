
package com.openexchange.saml.ucs.config;

import java.util.Set;
import com.openexchange.saml.SAMLConfig;

/**
 * {@link UCSSamlConfigImpl}
 *
 * @author <a href="mailto:felix.marx@open-xchange.com">Felix Marx</a>
 * @since v7.10.1
 */
public class UCSSamlConfigImpl implements SAMLConfig {

    public static class Builder {

        private String providerName;
        private String entityID;
        private String acsURL;
        private Binding logoutResponseBinding;
        private String idpAuthnURL;
        private String idpEntityID;
        private String idpLogoutURL;
        private String slsURL;
        private boolean supportSingleLogout;
        private boolean enableMetadataService;
        private String logoutResponseTemplate;
        private boolean autoLoginEnabled;
        private boolean allowUnsolicitedResponses;
        private boolean sessionIndexAutoLoginEnabled;
        private Set<String> hosts;

        public Builder providerName(String providerName) {
            this.providerName = providerName;
            return this;
        }

        public Builder entityID(String entityID) {
            this.entityID = entityID;
            return this;
        }

        public Builder acsURL(String acsURL) {
            this.acsURL = acsURL;
            return this;
        }

        public Builder logoutResponseBinding(Binding logoutResponseBinding) {
            this.logoutResponseBinding = logoutResponseBinding;
            return this;
        }

        public Builder idpAuthnURL(String idpAuthnURL) {
            this.idpAuthnURL = idpAuthnURL;
            return this;
        }

        public Builder idpEntityID(String idpEntityID) {
            this.idpEntityID = idpEntityID;
            return this;
        }

        public Builder idpLogoutURL(String idpLogoutURL) {
            this.idpLogoutURL = idpLogoutURL;
            return this;
        }

        public Builder slsURL(String slsURL) {
            this.slsURL = slsURL;
            return this;
        }

        public Builder supportSingleLogout(boolean supportSingleLogout) {
            this.supportSingleLogout = supportSingleLogout;
            return this;
        }

        public Builder enableMetadataService(boolean enableMetadataService) {
            this.enableMetadataService = enableMetadataService;
            return this;
        }

        public Builder logoutResponseTemplate(String logoutResponseTemplate) {
            this.logoutResponseTemplate = logoutResponseTemplate;
            return this;
        }

        public Builder autoLoginEnabled(boolean autoLoginEnabled) {
            this.autoLoginEnabled = autoLoginEnabled;
            return this;
        }

        public Builder allowUnsolicitedResponses(boolean allowUnsolicitedResponses) {
            this.allowUnsolicitedResponses = allowUnsolicitedResponses;
            return this;
        }

        public Builder sessionIndexAutoLoginEnabled(boolean sessionIndexAutoLoginEnabled) {
            this.sessionIndexAutoLoginEnabled = sessionIndexAutoLoginEnabled;
            return this;
        }

        public Builder hosts(Set<String> hosts) {
            this.hosts = hosts;
            return this;
        }

        public UCSSamlConfigImpl build() {
            return new UCSSamlConfigImpl(providerName, entityID, acsURL, logoutResponseBinding, idpAuthnURL, idpEntityID, idpLogoutURL, slsURL, supportSingleLogout, enableMetadataService, logoutResponseTemplate, autoLoginEnabled, allowUnsolicitedResponses, sessionIndexAutoLoginEnabled, hosts);
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    private final String providerName;
    private final String entityID;
    private final String acsURL;
    private final Binding logoutResponseBinding;
    private final String idpAuthnURL;
    private final String idpEntityID;
    private final String idpLogoutURL;
    private final String slsURL;
    private final boolean supportSingleLogout;
    private final boolean enableMetadataService;
    private final String logoutResponseTemplate;
    private final boolean autoLoginEnabled;
    private final boolean allowUnsolicitedResponses;
    private final boolean sessionIndexAutoLoginEnabled;
    private final Set<String> hosts;

    /**
     * Initializes a new {@link UCSSamlConfigImpl}.
     */
    UCSSamlConfigImpl(String providerName, String entityID, String acsURL, Binding logoutResponseBinding, String idpAuthnURL, String idpEntityID, String idpLogoutURL, String slsURL, boolean supportSingleLogout, boolean enableMetadataService, String logoutResponseTemplate, boolean autoLoginEnabled, boolean allowUnsolicitedResponses, boolean sessionIndexAutoLoginEnabled, Set<String> hosts) {
        super();
        this.providerName = providerName;
        this.entityID = entityID;
        this.acsURL = acsURL;
        this.logoutResponseBinding = logoutResponseBinding;
        this.idpAuthnURL = idpAuthnURL;
        this.idpEntityID = idpEntityID;
        this.idpLogoutURL = idpLogoutURL;
        this.slsURL = slsURL;
        this.supportSingleLogout = supportSingleLogout;
        this.enableMetadataService = enableMetadataService;
        this.logoutResponseTemplate = logoutResponseTemplate;
        this.autoLoginEnabled = autoLoginEnabled;
        this.allowUnsolicitedResponses = allowUnsolicitedResponses;
        this.sessionIndexAutoLoginEnabled = sessionIndexAutoLoginEnabled;
        this.hosts = hosts;
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    public String getEntityID() {
        return entityID;
    }

    @Override
    public String getAssertionConsumerServiceURL() {
        return acsURL;
    }

    @Override
    public String getSingleLogoutServiceURL() {
        return slsURL;
    }

    @Override
    public Binding getLogoutResponseBinding() {
        return logoutResponseBinding;
    }

    @Override
    public String getIdentityProviderEntityID() {
        return idpEntityID;
    }

    @Override
    public String getIdentityProviderAuthnURL() {
        return idpAuthnURL;
    }

    @Override
    public boolean singleLogoutEnabled() {
        return supportSingleLogout;
    }

    @Override
    public String getIdentityProviderLogoutURL() {
        return idpLogoutURL;
    }

    @Override
    public boolean enableMetadataService() {
        return enableMetadataService;
    }

    @Override
    public String getLogoutResponseTemplate() {
        return logoutResponseTemplate;
    }

    @Override
    public boolean isAutoLoginEnabled() {
        return autoLoginEnabled;
    }

    @Override
    public boolean isAllowUnsolicitedResponses() {
        return allowUnsolicitedResponses;
    }

    @Override
    public boolean isSessionIndexAutoLoginEnabled() {
        return sessionIndexAutoLoginEnabled;
    }

    @Override
    public Set<String> getHosts() {
        return hosts;
    }
}
