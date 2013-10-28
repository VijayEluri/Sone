/*
 * Sone - CreateReplyCommandTest.java - Copyright © 2013 David Roden
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

package net.pterodactylus.sone.fcp;

import static java.lang.System.currentTimeMillis;
import static net.pterodactylus.sone.data.Mocks.mockCore;
import static net.pterodactylus.sone.data.Mocks.mockDatabase;
import static net.pterodactylus.sone.data.Mocks.mockLocalSone;
import static net.pterodactylus.sone.data.Mocks.mockPost;
import static net.pterodactylus.sone.freenet.fcp.Command.AccessType.DIRECT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.when;

import net.pterodactylus.sone.core.Core;
import net.pterodactylus.sone.data.PostReply;
import net.pterodactylus.sone.data.Sone;
import net.pterodactylus.sone.database.Database;
import net.pterodactylus.sone.database.PostReplyBuilder.PostReplyCreated;
import net.pterodactylus.sone.freenet.SimpleFieldSetBuilder;
import net.pterodactylus.sone.freenet.fcp.Command.Response;
import net.pterodactylus.sone.freenet.fcp.FcpException;

import freenet.support.SimpleFieldSet;

import com.google.common.base.Optional;
import org.junit.Test;

/**
 * Unit test for {@link CreateReplyCommand}.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class CreateReplyCommandTest {

	private final long now = currentTimeMillis();
	private final Database database = mockDatabase();
	private final Core core = mockCore(database);
	private final CreateReplyCommand createReplyCommand = new CreateReplyCommand(core);

	@Test
	public void verifyThatCreatingAFullySpecifiedReplyWorks() throws FcpException {
		Sone sone = mockLocalSone(core, "SoneId");
		mockPost(core, sone, "PostId");
		CapturingPostReplyCreated capturingPostReplyCreated = new CapturingPostReplyCreated();
		when(core.postReplyCreated()).thenReturn(Optional.<PostReplyCreated>of(capturingPostReplyCreated));
		SimpleFieldSet createReplyFieldSet = new SimpleFieldSetBuilder()
				.put("Message", "CreateReply")
				.put("Sone", "SoneId")
				.put("Post", "PostId")
				.put("Text", "Text of the reply.")
				.get();
		Response response = createReplyCommand.execute(createReplyFieldSet, null, DIRECT);
		assertThat(response, notNullValue());
		assertThat(capturingPostReplyCreated.postReply, notNullValue());
		assertThat(capturingPostReplyCreated.postReply.getId(), notNullValue());
		assertThat(capturingPostReplyCreated.postReply.getPostId(), is("PostId"));
		assertThat(capturingPostReplyCreated.postReply.getSone(), is(sone));
		assertThat(capturingPostReplyCreated.postReply.getTime(), allOf(greaterThanOrEqualTo(now), lessThanOrEqualTo(currentTimeMillis())));
		assertThat(capturingPostReplyCreated.postReply.getText(), is("Text of the reply."));
		assertThat(response.getReplyParameters(), notNullValue());
		assertThat(response.getReplyParameters().get("Message"), is("ReplyCreated"));
		assertThat(response.getReplyParameters().get("Reply"), is(capturingPostReplyCreated.postReply.getId()));
	}

	@Test(expected = FcpException.class)
	public void verifyThatCreatingAReplyWithoutSoneCausesAnError() throws FcpException {
		Sone sone = mockLocalSone(core, "SoneId");
		mockPost(core, sone, "PostId");
		CapturingPostReplyCreated capturingPostReplyCreated = new CapturingPostReplyCreated();
		when(core.postReplyCreated()).thenReturn(Optional.<PostReplyCreated>of(capturingPostReplyCreated));
		SimpleFieldSet createReplyFieldSet = new SimpleFieldSetBuilder()
				.put("Message", "CreateReply")
				.put("Post", "PostId")
				.put("Text", "Text of the reply.")
				.get();
		createReplyCommand.execute(createReplyFieldSet, null, DIRECT);
	}

	@Test(expected = FcpException.class)
	public void verifyThatCreatingAReplyWithoutPostCausesAnError() throws FcpException {
		mockLocalSone(core, "SoneId");
		CapturingPostReplyCreated capturingPostReplyCreated = new CapturingPostReplyCreated();
		when(core.postReplyCreated()).thenReturn(Optional.<PostReplyCreated>of(capturingPostReplyCreated));
		SimpleFieldSet createReplyFieldSet = new SimpleFieldSetBuilder()
				.put("Message", "CreateReply")
				.put("Sone", "SoneId")
				.put("Text", "Text of the reply.")
				.get();
		createReplyCommand.execute(createReplyFieldSet, null, DIRECT);
	}

	@Test(expected = FcpException.class)
	public void verifyThatCreatingAReplyWithoutTextCausesAnError() throws FcpException {
		Sone sone = mockLocalSone(core, "SoneId");
		mockPost(core, sone, "PostId");
		CapturingPostReplyCreated capturingPostReplyCreated = new CapturingPostReplyCreated();
		when(core.postReplyCreated()).thenReturn(Optional.<PostReplyCreated>of(capturingPostReplyCreated));
		SimpleFieldSet createReplyFieldSet = new SimpleFieldSetBuilder()
				.put("Message", "CreateReply")
				.put("Sone", "SoneId")
				.put("Post", "PostId")
				.get();
		createReplyCommand.execute(createReplyFieldSet, null, DIRECT);
	}

	private class CapturingPostReplyCreated implements PostReplyCreated {

		public PostReply postReply;

		@Override
		public void postReplyCreated(PostReply postReply) {
			this.postReply = postReply;
		}

	}
}