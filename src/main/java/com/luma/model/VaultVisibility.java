package com.luma.model;

public enum VaultVisibility {
    PRIVATE,           // Only visible to the owner
    TRUSTED_CIRCLE,    // Visible to designated trusted circle usernames
    TEMPORARY_ACCESS   // Accessible for a specific time window, then auto-revoked
}
