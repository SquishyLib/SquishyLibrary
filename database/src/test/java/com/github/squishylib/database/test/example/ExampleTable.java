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

package com.github.squishylib.database.test.example;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.RecordFieldPool;
import com.github.squishylib.database.test.TestRecord;
import org.jetbrains.annotations.NotNull;

public class ExampleTable extends Table<ExampleRecord> {

    public static final @NotNull String TABLE_NAME = "example";

    @Override
    public @NotNull String getName() {
        return ExampleTable.TABLE_NAME;
    }

    @NotNull
    @Override
    public ExampleRecord createEmptyRecord(@NotNull RecordFieldPool pool) {
        return new ExampleRecord(pool.getString(TestRecord.IDENTIFIER_KEY));
    }
}
