package com.publicis.sapient.Ticket_Tool.controllers;

/**
 * Sync is now handled via Kafka.
 * - SeatSyncConsumer listens on 'seat-sync-events' topic
 *   and updates the read DB automatically.
 * - This REST controller is no longer needed.
 */
