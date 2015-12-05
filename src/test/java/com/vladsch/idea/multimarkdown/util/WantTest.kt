/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.util

import org.junit.Assert.assertEquals
import org.junit.Test

class WantTest {

//    @Test
//    fun test_BitFieldOverflow___ToFail() {
//        val bitField = BitField(16, BitField(8, BitField(8)))
//        val bitField2 = BitField(16, BitField(8, BitField(9)))
//
//        val opt:Int = Want(Links.NONE)
//        assertEquals(false, Want.links(opt))
//        assertEquals(Links.NONE, Want.linksType(opt))
//    }

    @Test
    fun testLinksTypeNone() {
        val opt:Int = Want(Links.NONE)
        assertEquals(false, Want.links(opt))
        assertEquals(Links.NONE, Want.linksType(opt))
    }

    @Test
    fun testLinksTypeURL() {
        val opt:Int = Want(Links.URL)
        assertEquals(true, Want.links(opt))
        assertEquals(Links.URL, Want.linksType(opt))
    }

    @Test
    fun testLinksTypeDefault() {
        val opt:Int = Want()
        assertEquals(true, Want.links(opt))
        assertEquals(Links.URL, Want.linksType(opt))
    }

    @Test
    fun testDefaultMatch() {
        val opt:Int = Want()
        assertEquals(false, Want.looseMatch(opt))
        assertEquals(false, Want.completionMatch(opt))
        assertEquals(Match.EXACT, Want.matchType(opt))
    }

    @Test
    fun testExactMatch() {
        val opt:Int = Want(Match.EXACT)
        assertEquals(false, Want.looseMatch(opt))
        assertEquals(false, Want.completionMatch(opt))
        assertEquals(Match.EXACT, Want.matchType(opt))
    }

    @Test
    fun testLooseMatch() {
        val opt:Int = Want(Match.LOOSE)
        assertEquals(true, Want.looseMatch(opt))
        assertEquals(false, Want.completionMatch(opt))
        assertEquals(Match.LOOSE, Want.matchType(opt))
    }

    @Test
    fun testCompletionMatch() {
        val opt:Int = Want(Match.COMPLETION)
        assertEquals(false, Want.looseMatch(opt))
        assertEquals(true, Want.completionMatch(opt))
        assertEquals(Match.COMPLETION, Want.matchType(opt))
    }

    @Test
    fun testLocal_NONE() {
        val opt:Int = Want(Local.NONE)
        assertEquals(false, Want.local(opt))
        assertEquals(false, Want.localREF(opt))
        assertEquals(false, Want.localURI(opt))
        assertEquals(false, Want.localURL(opt))
        assertEquals(Local.NONE, Want.localType(opt))
        assertEquals(Remote.REF, Want.remoteType(opt))
        assertEquals(Links.URL, Want.linksType(opt))
    }

    @Test
    fun testLocal_REF() {
        val opt:Int = Want(Local.REF)
        assertEquals(true, Want.local(opt))
        assertEquals(true, Want.localREF(opt))
        assertEquals(false, Want.localURI(opt))
        assertEquals(false, Want.localURL(opt))
        assertEquals(Local.REF, Want.localType(opt))
        assertEquals(Remote.NONE, Want.remoteType(opt))
        assertEquals(Links.NONE, Want.linksType(opt))
    }

    @Test
    fun testLocal_Default() {
        val opt:Int = Want()
        assertEquals(true, Want.local(opt))
        assertEquals(true, Want.localREF(opt))
        assertEquals(false, Want.localURI(opt))
        assertEquals(false, Want.localURL(opt))
        assertEquals(Local.REF, Want.localType(opt))
        assertEquals(Remote.REF, Want.remoteType(opt))
        assertEquals(Links.URL, Want.linksType(opt))
    }

    @Test
    fun testLocal_URI() {
        val opt:Int = Want(Local.URI)
        assertEquals(true, Want.local(opt))
        assertEquals(false, Want.localREF(opt))
        assertEquals(true, Want.localURI(opt))
        assertEquals(false, Want.localURL(opt))
        assertEquals(Local.URI, Want.localType(opt))
        assertEquals(Remote.NONE, Want.remoteType(opt))
        assertEquals(Links.NONE, Want.linksType(opt))
    }

    @Test
    fun testLocal_URL() {
        val opt:Int = Want(Local.URL)
        assertEquals(true, Want.local(opt))
        assertEquals(false, Want.localREF(opt))
        assertEquals(false, Want.localURI(opt))
        assertEquals(true, Want.localURL(opt))
        assertEquals(Local.URL, Want.localType(opt))
    }

    @Test
    fun testRemote_NONE() {
        val opt:Int = Want(Remote.NONE)
        assertEquals(false, Want.remote(opt))
        assertEquals(false, Want.remoteREF(opt))
        assertEquals(false, Want.remoteURI(opt))
        assertEquals(false, Want.remoteURL(opt))
        assertEquals(Remote.NONE, Want.remoteType(opt))
    }

    @Test
    fun testRemote_REF() {
        val opt:Int = Want(Remote.REF)
        assertEquals(true, Want.remote(opt))
        assertEquals(true, Want.remoteREF(opt))
        assertEquals(false, Want.remoteURI(opt))
        assertEquals(false, Want.remoteURL(opt))
        assertEquals(Remote.REF, Want.remoteType(opt))
        assertEquals(Local.NONE, Want.localType(opt))
        assertEquals(Links.NONE, Want.linksType(opt))
    }

    @Test
    fun testRemote_Default() {
        val opt:Int = Want()
        assertEquals(true, Want.remote(opt))
        assertEquals(true, Want.remoteREF(opt))
        assertEquals(false, Want.remoteURI(opt))
        assertEquals(false, Want.remoteURL(opt))
        assertEquals(Remote.REF, Want.remoteType(opt))
        assertEquals(Local.REF, Want.localType(opt))
        assertEquals(Links.URL, Want.linksType(opt))
    }

    @Test
    fun testRemote_URI() {
        val opt:Int = Want(Remote.URI)
        assertEquals(true, Want.remote(opt))
        assertEquals(false, Want.remoteREF(opt))
        assertEquals(true, Want.remoteURI(opt))
        assertEquals(false, Want.remoteURL(opt))
        assertEquals(Remote.URI, Want.remoteType(opt))
        assertEquals(Local.NONE, Want.localType(opt))
        assertEquals(Links.NONE, Want.linksType(opt))
    }

    @Test
    fun testRemote_URL() {
        val opt:Int = Want(Remote.URL)
        assertEquals(true, Want.remote(opt))
        assertEquals(false, Want.remoteREF(opt))
        assertEquals(false, Want.remoteURI(opt))
        assertEquals(true, Want.remoteURL(opt))
        assertEquals(Remote.URL, Want.remoteType(opt))
        assertEquals(Local.NONE, Want.localType(opt))
        assertEquals(Links.NONE, Want.linksType(opt))
    }

    @Test
    fun testDefaults() {
        val opt:Int = Want()
        assertEquals(true, Want.links(opt))
        assertEquals(Links.URL, Want.linksType(opt))

        assertEquals(false, Want.looseMatch(opt))
        assertEquals(false, Want.completionMatch(opt))
        assertEquals(Match.EXACT, Want.matchType(opt))

        assertEquals(true, Want.local(opt))
        assertEquals(true, Want.localREF(opt))
        assertEquals(false, Want.localURI(opt))
        assertEquals(false, Want.localURL(opt))
        assertEquals(Local.REF, Want.localType(opt))

        assertEquals(true, Want.remote(opt))
        assertEquals(true, Want.remoteREF(opt))
        assertEquals(false, Want.remoteURI(opt))
        assertEquals(false, Want.remoteURL(opt))
        assertEquals(Remote.REF, Want.remoteType(opt))
    }

    @Test
    fun testImageLinkRenderer() {
        val opt:Int = Want(Local.URI, Remote.URI, Links.NONE)

        assertEquals(false, Want.links(opt))
        assertEquals(Links.NONE, Want.linksType(opt))

        assertEquals(false, Want.looseMatch(opt))
        assertEquals(false, Want.completionMatch(opt))
        assertEquals(Match.EXACT, Want.matchType(opt))

        assertEquals(true, Want.local(opt))
        assertEquals(false, Want.localREF(opt))
        assertEquals(true, Want.localURI(opt))
        assertEquals(false, Want.localURL(opt))
        assertEquals(Local.URI, Want.localType(opt))

        assertEquals(true, Want.remote(opt))
        assertEquals(false, Want.remoteREF(opt))
        assertEquals(true, Want.remoteURI(opt))
        assertEquals(false, Want.remoteURL(opt))
        assertEquals(Remote.URI, Want.remoteType(opt))
    }

    @Test
    fun testLinkRenderer() {
        val opt:Int = Want(Local.URI, Remote.URL, Links.URL)

        assertEquals(true, Want.links(opt))
        assertEquals(Links.URL, Want.linksType(opt))

        assertEquals(false, Want.looseMatch(opt))
        assertEquals(false, Want.completionMatch(opt))
        assertEquals(Match.EXACT, Want.matchType(opt))

        assertEquals(true, Want.local(opt))
        assertEquals(false, Want.localREF(opt))
        assertEquals(true, Want.localURI(opt))
        assertEquals(false, Want.localURL(opt))
        assertEquals(Local.URI, Want.localType(opt))

        assertEquals(true, Want.remote(opt))
        assertEquals(false, Want.remoteREF(opt))
        assertEquals(false, Want.remoteURI(opt))
        assertEquals(true, Want.remoteURL(opt))
        assertEquals(Remote.URL, Want.remoteType(opt))
    }

    @Test
    fun testHttpAll() {
        val opt:Int = Want(Local.URL, Remote.URL, Links.URL)

        assertEquals(true, Want.links(opt))
        assertEquals(Links.URL, Want.linksType(opt))

        assertEquals(false, Want.looseMatch(opt))
        assertEquals(false, Want.completionMatch(opt))
        assertEquals(Match.EXACT, Want.matchType(opt))

        assertEquals(true, Want.local(opt))
        assertEquals(false, Want.localREF(opt))
        assertEquals(false, Want.localURI(opt))
        assertEquals(true, Want.localURL(opt))
        assertEquals(Local.URL, Want.localType(opt))

        assertEquals(true, Want.remote(opt))
        assertEquals(false, Want.remoteREF(opt))
        assertEquals(false, Want.remoteURI(opt))
        assertEquals(true, Want.remoteURL(opt))
        assertEquals(Remote.URL, Want.remoteType(opt))
    }

    @Test
    fun test_DupeOptsMatch() {
        val opt1:Int = Want(Match.COMPLETION, Match.LOOSE)
        val opt2:Int = Want(Match.LOOSE, Match.COMPLETION)

        assertEquals(true, Want.looseMatch(opt1))
        assertEquals(true, Want.looseMatch(opt2))
        assertEquals(true, Want.completionMatch(opt1))
        assertEquals(true, Want.completionMatch(opt2))
        assertEquals(opt1, opt2)
    }

//    @Test
//    fun test_DupeOptsLinks___ToFail() {
//        val opt:Int = Want(Links.URL, Links.NONE)
//    }
//
//    @Test
//    fun test_DupeOptsMatch1___ToFail() {
//        val opt:Int = Want(Match.EXACT, Match.LOOSE)
//    }
//
//    @Test
//    fun test_DupeOptsMatch2___ToFail() {
//        val opt:Int = Want(Match.EXACT, Match.COMPLETION)
//    }
//
//    @Test
//    fun test_DupeOptsMatch3___ToFail() {
//        val opt:Int = Want(Match.LOOSE, Match.EXACT)
//    }
//
//    @Test
//    fun test_DupeOptsMatch4___ToFail() {
//        val opt:Int = Want(Match.COMPLETION, Match.EXACT)
//    }
//
//    @Test
//    fun test_DupeOptsLocal___ToFail() {
//        val opt:Int = Want(Local.REF, Local.NONE)
//    }
//
//    @Test
//    fun test_DupeOptsRemote___ToFail() {
//        val opt:Int = Want(Remote.REF, Remote.NONE)
//    }
}