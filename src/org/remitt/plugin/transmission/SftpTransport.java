/*
 * $Id$
 *
 * Authors:
 *      Jeff Buchbinder <jeff@freemedsoftware.org>
 *
 * REMITT Electronic Medical Information Translation and Transmission
 * Copyright (C) 1999-2009 FreeMED Software Foundation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.remitt.plugin.transmission;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.remitt.prototype.PluginInterface;
import org.remitt.server.Configuration;

import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;

public class SftpTransport implements PluginInterface {

	@Override
	public String getInputFormat() {
		return "text";
	}

	@Override
	public HashMap<String, String> getOptions() {
		return null;
	}

	@Override
	public String getOutputFormat() {
		return null;
	}

	@Override
	public String[] getPluginConfigurationOptions() {
		return new String[] { "sftpUsername", "sftpPassword", "sftpHost",
				"sftpPort", "sftpPath" };
	}

	@Override
	public String getPluginName() {
		return "SftpTransport";
	}

	@Override
	public Double getPluginVersion() {
		return 0.1;
	}

	@Override
	public byte[] render(Integer jobId, String input, String option)
			throws Exception {
		// TODO: get username
		String userName = "FIXME";

		// TODO: create destination file path
		String tempPathName = "FIXME";

		SshClient ssh = new SshClient();

		String host = Configuration.getPluginOption(this, userName, "sftpHost");
		Integer port = Integer.parseInt(Configuration.getPluginOption(this,
				userName, "sftpPort"));
		String sftpUser = Configuration.getPluginOption(this, userName,
				"sftpUser");
		String sftpPassword = Configuration.getPluginOption(this, userName,
				"sftpPassword");
		String sftpPath = Configuration.getPluginOption(this, userName,
				"sftpPath");

		// Perform initial connection
		ssh.connect(host, port);

		// Authenticate
		PasswordAuthenticationClient passwordAuthenticationClient = new PasswordAuthenticationClient();
		passwordAuthenticationClient.setUsername(sftpUser);
		passwordAuthenticationClient.setPassword(sftpPassword);
		int result = ssh.authenticate(passwordAuthenticationClient);
		if (result != AuthenticationProtocolState.COMPLETE) {
			throw new Exception("Login to " + host + ":" + port + " "
					+ sftpUser + "/" + sftpPassword + " failed");
		}
		// Open the SFTP channel
		SftpClient client = ssh.openSftpClient();

		if (sftpPath != null && sftpPath != "") {
			client.cd(sftpPath);
		}

		// Convert string to input stream for transfer
		ByteArrayInputStream bs = new ByteArrayInputStream(input.getBytes());

		// Send the file
		client.put(bs, tempPathName);

		// Disconnect
		client.quit();
		ssh.disconnect();

		return new String("").getBytes();
	}
}
