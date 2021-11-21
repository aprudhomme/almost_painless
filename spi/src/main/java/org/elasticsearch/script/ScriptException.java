package org.elasticsearch.script;

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.*;

/**
 * Exception from a scripting engine.
 * <p>
 * A ScriptException has the following components:
 * <ul>
 *   <li>{@code message}: A short and simple summary of what happened, such as "compile error".
 *   <li>{@code cause}: The underlying cause of the exception.
 *   <li>{@code scriptStack}: An implementation-specific "stacktrace" for the error in the script.
 *   <li>{@code script}: Identifier for which script failed.
 *   <li>{@code lang}: Scripting engine language, such as "painless"
 * </ul>
 */
@SuppressWarnings("serial")
public class ScriptException extends RuntimeException {
    private final List<String> scriptStack;
    private final String script;
    private final String lang;
    private final Position pos;

    private final Map<String, List<String>> metadata = new HashMap<>();

    /**
     * Create a new ScriptException.
     * @param message A short and simple summary of what happened, such as "compile error".
     *                Must not be {@code null}.
     * @param cause The underlying cause of the exception. Must not be {@code null}.
     * @param scriptStack An implementation-specific "stacktrace" for the error in the script.
     *                Must not be {@code null}, but can be empty (though this should be avoided if possible).
     * @param script Identifier for which script failed. Must not be {@code null}.
     * @param lang Scripting engine language, such as "painless". Must not be {@code null}.
     * @param pos Position of error within script, may be {@code null}.
     * @throws NullPointerException if any parameters are {@code null} except pos.
     */
    public ScriptException(String message, Throwable cause, List<String> scriptStack, String script, String lang, Position pos) {
        super(Objects.requireNonNull(message), Objects.requireNonNull(cause));
        this.scriptStack = Collections.unmodifiableList(Objects.requireNonNull(scriptStack));
        this.script = Objects.requireNonNull(script);
        this.lang = Objects.requireNonNull(lang);
        this.pos = pos;
    }

    /**
     * Create a new ScriptException with null Position.
     */
    public ScriptException(String message, Throwable cause, List<String> scriptStack, String script, String lang) {
        this(message, cause, scriptStack, script, lang, null);
    }

    /**
     * Returns the stacktrace for the error in the script.
     * @return a read-only list of frames, which may be empty.
     */
    public List<String> getScriptStack() {
        return scriptStack;
    }

    /**
     * Returns the identifier for which script.
     * @return script's name or source text that identifies the script.
     */
    public String getScript() {
        return script;
    }

    /**
     * Returns the language of the script.
     * @return the {@code lang} parameter of the scripting engine.
     */
    public String getLang() {
        return lang;
    }

    /**
     * Returns the position of the error.
     */
    public Position getPos() {
        return pos;
    }

    public static class Position {
        public final int offset;
        public final int start;
        public final int end;

        public Position(int offset, int start, int end) {
            this.offset = offset;
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Position position = (Position) o;
            return offset == position.offset && start == position.start && end == position.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(offset, start, end);
        }
    }

    /**
     * Adds a new piece of metadata with the given key.
     * If the provided key is already present, the corresponding metadata will be replaced
     */
    public void addMetadata(String key, List<String> values) {
        //we need to enforce this otherwise bw comp doesn't work properly, as "es." was the previous criteria to split headers in two sets
        if (key.startsWith("es.") == false) {
            throw new IllegalArgumentException("exception metadata must start with [es.], found [" + key + "] instead");
        }
        this.metadata.put(key, values);
    }

    /**
     * Returns the list of metadata values for the given key or {@code null} if no metadata for the
     * given key exists.
     */
    public List<String> getMetadata(String key) {
        return metadata.get(key);
    }
}
