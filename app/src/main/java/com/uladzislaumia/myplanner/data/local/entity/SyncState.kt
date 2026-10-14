package com.uladzislaumia.myplanner.data.local.entity

/** Sync status of a local row relative to Firestore. */
enum class SyncState {
    /** Local row matches the cloud. */
    SYNCED,

    /** Created or edited locally, not uploaded yet. */
    PENDING_UPSERT,

    /** Deleted locally: hidden from UI, removed from Room once the deletion reaches Firestore. */
    PENDING_DELETE,
}
