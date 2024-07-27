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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Request<R> {

    public interface AsyncRequest<R> {
        @Nullable R execute();
    }

    public interface Listener<R> {
        void onComplete(R result);
    }

    private final @NotNull AsyncRequest<R> executable;
    private final @NotNull List<Listener<R>> listenerList;

    public Request(@NotNull AsyncRequest<R> executable) {
        this.executable = executable;
        this.listenerList = new ArrayList<>();
    }

    public @NotNull Request<R> addListener(@NotNull Listener<R> listener) {
        this.listenerList.add(listener);
        return this;
    }

    public void executeSync() {
        R result = this.executable.execute();
        this.listenerList.forEach(listener -> listener.onComplete(result));
    }
}
