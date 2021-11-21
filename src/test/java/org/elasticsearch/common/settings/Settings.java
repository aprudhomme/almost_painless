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
package org.elasticsearch.common.settings;

import org.elasticsearch.painless.CompilerSettings;

// minimal setting class that supports only the painless config keys
public class Settings {
    public static final Settings DEFAULT = new Settings(
            CompilerSettings.DEFAULT_REGEX_ENABLED, CompilerSettings.DEFAULT_REGEX_LIMIT_FACTOR);

    private final CompilerSettings.RegexEnabled enabled;
    private final int factor;

    public Settings(CompilerSettings.RegexEnabled enabled, int factor) {
        this.enabled = enabled;
        this.factor = factor;
    }

    public CompilerSettings.RegexEnabled getEnabled() {
        return enabled;
    }

    public int getFactor() {
        return factor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        int factor = CompilerSettings.DEFAULT_REGEX_LIMIT_FACTOR;
        CompilerSettings.RegexEnabled enabled = CompilerSettings.DEFAULT_REGEX_ENABLED;

        // implement put methods only for needed keys
        public Builder put(String key, String value) {
            enabled = CompilerSettings.RegexEnabled.parse(value);
            return this;
        }

        public Builder put(String key, boolean value) {
            if (value) {
                enabled = CompilerSettings.RegexEnabled.TRUE;
            } else {
                enabled = CompilerSettings.RegexEnabled.FALSE;
            }
            return this;
        }

        public Builder put(String key, int value) {
            factor = value;
            return this;
        }

        public Settings build() {
            return new Settings(enabled, factor);
        }
    }
}
