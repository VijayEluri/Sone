/*
 * Sone - TrustTest.java - Copyright © 2013 David Roden
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.pterodactylus.sone.freenet.wot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

/**
 * Unit test for {@link Trust}.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class TrustTest {

	@Test
	public void trustCanBeCreated() {
		Trust trust = new Trust(5, 17, 2);
		assertThat(trust.getExplicit(), is(5));
		assertThat(trust.getImplicit(), is(17));
		assertThat(trust.getDistance(), is(2));
	}

	@Test
	public void nullTrustCanBeCreated() {
		Trust trust = new Trust(null, null, null);
		assertThat(trust.getExplicit(), nullValue());
		assertThat(trust.getImplicit(), nullValue());
		assertThat(trust.getDistance(), nullValue());
	}

}