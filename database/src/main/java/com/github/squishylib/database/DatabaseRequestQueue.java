/*
 * Java configuration and database library.
 * Copyright (C) 2024  Smuddgge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.squishylib.database;

import com.github.squishylib.common.CompletableFuture;
import com.github.squishylib.common.task.TaskContainer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This should be the base of every database type.
 * <p>
 * Uses a queue structure to manage requests.
 */
public class DatabaseRequestQueue extends TaskContainer {

    private final @NotNull Duration timeBetweenRequests;
    private final long maxRequestsPending;

    private final @NotNull Queue<Request<?>> queue;
    private boolean running;
    private boolean sentMaximumRequestsMessage;

    public DatabaseRequestQueue(@NotNull Duration timeBetweenRequests, @NotNull long maxRequestsPending) {
        this.timeBetweenRequests = timeBetweenRequests;
        this.maxRequestsPending = maxRequestsPending;
        this.queue = new ConcurrentLinkedQueue<>();
        this.running = false;
        this.sentMaximumRequestsMessage = false;
    }

    /**
     * Used to add a request to the queue.
     * <p>
     * The request will then be executed when the database connection is ready.
     * <p>
     * The request may be cancelled due to a request queue limit.
     * If so the future will be cancelled.
     *
     * @param request The request to add to the queue.
     * @param <R>     The type of result.
     * @return The future result when executed.
     */
    public @NotNull <R> CompletableFuture<R> addRequest(@NotNull Request<R> request) {

        // Check if the queue has reached max requests.
        if (this.queue.size() >= this.maxRequestsPending) {

            // If we have already sent an error message, complete this request with a null value.
            if (this.sentMaximumRequestsMessage) {
                CompletableFuture<R> future = new CompletableFuture<>();
                future.cancel(true);
                return future;
            }

            // Send error message.
            this.sentMaximumRequestsMessage = true;
            throw new DatabaseException(this, "add", "Reached maximum database requests. The queue is currently at " + queue.size());
        }

        // Ensure value false.
        this.sentMaximumRequestsMessage = false;

        // Create the future of the result.
        CompletableFuture<R> future = new CompletableFuture<>();

        // Listen for when the result is completed.
        request.addListener(future::complete);

        // Add the request to the queue.
        this.queue.add(request);

        // Is the queue being iterated?
        if (this.running) return future;

        // Set running to true.
        this.running = true;

        // Start the iteration of the queue.
        this.startNextTask();
        return future;
    }

    private void startNextTask() {
        new Thread(() -> {

            try {
                // Get the next request.
                Request<?> request = this.queue.poll();

                // Double check it exists.
                if (request == null) {
                    this.running = false;
                    return;
                }

                // Execute and wait for result.
                request.executeSync();

                // Wait for duration.
                Thread.sleep(this.timeBetweenRequests.toMillis());

                // Are there no tasks left?
                if (this.queue.isEmpty()) {
                    this.running = false;
                    return;
                }

                this.startNextTask();

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "startNextTask", "Unable to execute next request in queue.");
            }

        }).start();
    }
}
