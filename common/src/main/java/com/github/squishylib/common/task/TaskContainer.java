/*
 * Kerb
 * Event and request distributor server software.
 *
 * Copyright (C) 2023  Smuddgge
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

package com.github.squishylib.common.task;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a task container.
 * This can be implemented to run tasks in the class instance.
 */
@Deprecated
public class TaskContainer {

    public static final int DEFAULT_TIMEOUT_MILLIS = 100;
    private static Map<String, Task> globalTaskMap;

    private final int timeoutMillis;
    private @NotNull Map<String, Task> localTaskMap;

    public TaskContainer() {
        this.timeoutMillis = DEFAULT_TIMEOUT_MILLIS;
        this.localTaskMap = new HashMap<>();
    }

    public TaskContainer(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        this.localTaskMap = new HashMap<>();
    }

    public @NotNull TaskContainer runTaskLater(@NotNull String identifier,
                                               @NotNull Duration duration,
                                               @NotNull Runnable runnable) {

        // Check if the identifier already exists.

        return this;
    }

    /**
     * Run a task in the future.
     *
     * @param runnable   The instance of the task.
     * @param duration   The duration to wait.
     * @param identifier The identifier to set the task.
     * @return This instance.
     */
    protected @NotNull TaskContainer runTask(@NotNull Runnable runnable, @NotNull Duration duration, @NotNull String identifier) {

        // Check if the identifier already exists.
        if (this.localTaskMap.containsKey(identifier)) {
            throw new RuntimeException("Identifier already exists within task container.");
        }

        // Start the thread.
        new Thread(() -> {

            // Get the current time.
            long from = System.currentTimeMillis();

            // Set the running variable.
            AtomicBoolean running = new AtomicBoolean(true);

            // Create the instance of the task.
            Task task = () -> running.set(false);
            this.localTaskMap.put(identifier, task);

            // Wait till duration has completed.
            while (running.get()) {
                try {

                    // Check if it's time to run the runnable.
                    if (System.currentTimeMillis() - from >= duration.toMillis()) break;

                    // Wait a few mills.
                    Thread.sleep(DEFAULT_TIMEOUT_MILLIS);

                    // Check if the task was canceled.
                    if (!running.get()) {
                        this.localTaskMap.remove(identifier);
                        return;
                    }

                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            }

            // Check if the task was canceled.
            if (!running.get()) {
                this.localTaskMap.remove(identifier);
                return;
            }

            // Run the task.
            this.localTaskMap.remove(identifier);
            runnable.run();

        }).start();

        return this;
    }

    /**
     * Used to stop a single task.
     *
     * @param identifier The instance of the task's identifier.
     * @return This instance.
     */
    public @NotNull TaskContainer stopTask(@NotNull String identifier) {
        Task task = this.localTaskMap.get(identifier);

        // Check if the task doesn't exist.
        if (task == null) return this;

        // Cancel the task.
        task.cancel();
        this.localTaskMap.remove(identifier);
        return this;
    }

    /**
     * Used to stop all tasks in the container.
     *
     * @return This instance.
     */
    public @NotNull TaskContainer stopAllTasks() {
        for (Task task : this.localTaskMap.values()) {
            task.cancel();
        }

        this.localTaskMap = new HashMap<>();
        return this;
    }
}
