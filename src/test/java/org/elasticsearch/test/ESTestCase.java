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

package org.elasticsearch.test;

import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.generators.RandomNumbers;
import com.carrotsearch.randomizedtesting.generators.RandomPicks;
import org.apache.lucene.util.LuceneTestCase;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

// Base test case implementation with only what is needed to run existing test
public class ESTestCase extends LuceneTestCase {

    /**
     * An alias for {@link #randomIntBetween(int, int)}.
     */
    public static int between(int min, int max) {
        return randomIntBetween(min, max);
    }

    public static String randomAlphaOfLength(int codeUnits) {
        return RandomizedTest.randomAsciiOfLength(codeUnits);
    }

    /** Pick a random object from the given array. The array must not be empty. */
    public static <T> T randomFrom(T... array) {
        return randomFrom(random(), array);
    }

    /** Pick a random object from the given array. The array must not be empty. */
    public static <T> T randomFrom(Random random, T... array) {
        return RandomPicks.randomFrom(random, array);
    }

    public static int randomInt() {
        return random().nextInt();
    }

    /**
     * A random integer from <code>min</code> to <code>max</code> (inclusive).
     */
    public static int randomIntBetween(int min, int max) {
        return RandomNumbers.randomIntBetween(random(), min, max);
    }

    public static long randomLong() {
        return random().nextLong();
    }

    public static String randomRealisticUnicodeOfLength(int codeUnits) {
        return RandomizedTest.randomRealisticUnicodeOfLength(codeUnits);
    }

    /**
     * helper to get a random value in a certain range that's different from the input
     */
    public static <T> T randomValueOtherThanMany(Predicate<T> input, Supplier<T> randomSupplier) {
        T randomValue = null;
        do {
            randomValue = randomSupplier.get();
        } while (input.test(randomValue));
        return randomValue;
    }

}
