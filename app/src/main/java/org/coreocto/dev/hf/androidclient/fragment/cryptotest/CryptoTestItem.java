package org.coreocto.dev.hf.androidclient.fragment.cryptotest;

public class CryptoTestItem {
    private boolean enabled;
    private String schemeName;

    public CryptoTestItem(String schemeName) {
        this.enabled = true;
        this.schemeName = schemeName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSchemeName() {
        return schemeName;
    }
}
