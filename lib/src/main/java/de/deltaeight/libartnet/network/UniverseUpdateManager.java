/*
 * LibArtNet
 *
 * Art-Net(TM) Designed by and Copyright Artistic Licence Holdings Ltd
 *
 * Copyright (c) 2019 Julian Rabe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.deltaeight.libartnet.network;

import java.time.Instant;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

class UniverseUpdateManager {

    // FIXME Need different data structure needed since TreeSet only allows one value per "key":
    private final TreeSet<UniverseUpdate> universeUpdates;
    private final HashMap<Integer, UniverseUpdate> universes;

    UniverseUpdateManager() {
        universeUpdates = new TreeSet<>();
        universes = new HashMap<>();
    }

    void universeUpdated(int uid) {
        universeUpdated(uid, Instant.now());
    }

    void universeUpdated(int uid, Instant updated) {
        UniverseUpdate universe = universes.get(uid);

        if (universe != null) {
            universeUpdates.remove(universe);
        }

        UniverseUpdate relation = new UniverseUpdate(uid, updated);
        universeUpdates.add(relation);
        universes.put(uid, relation);
    }

    void removeUniverse(int uid) {

        UniverseUpdate universe = universes.get(uid);

        if (universe != null) {
            universeUpdates.remove(universe);
        }

        universes.remove(uid);
    }

    SortedSet<UniverseUpdate> getUniversesUpdatedBefore(Instant then) {
        return this.universeUpdates.headSet(new UniverseUpdate(then));
    }

    class UniverseUpdate implements Comparable<UniverseUpdate> {

        private final int uid;
        private final Instant lastUpdated;

        private UniverseUpdate(Instant lastUpdated) {
            uid = -1;
            this.lastUpdated = lastUpdated;
        }

        private UniverseUpdate(int uid, Instant lastUpdated) {
            this.uid = uid;
            this.lastUpdated = lastUpdated;
        }

        int getUid() {
            return uid;
        }

        Instant getLastUpdated() {
            return lastUpdated;
        }

        @Override
        public int compareTo(UniverseUpdate o) {
            return lastUpdated.compareTo(o.lastUpdated);
        }
    }
}
