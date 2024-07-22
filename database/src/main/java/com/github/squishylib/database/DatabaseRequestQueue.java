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

    public @NotNull <R> CompletableFuture<R> add(@NotNull Request<R> request) {

        // Check if the queue has reached max requests.
        if (this.queue.size() >= this.maxRequestsPending) {

            // If we have already sent an error message, complete this request with a null value.
            if (this.sentMaximumRequestsMessage) {
                CompletableFuture<R> future = new CompletableFuture<>();
                future.complete(null);
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
