package com.cyndre.dvm.plugin.diff;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;

public class GitHelper {
	public static final int EXIT_SUCCESS = 0;

	public static void checkout(final String branch, final File cwd, final Log log)
	throws MojoExecutionException {
		log.debug(String.format("executing `git checkout %s` on %s", branch, cwd.getAbsolutePath()));
		
		try {
			final Commandline cl = new Commandline("git");
			cl.addArguments(new String[] { "checkout", branch });
			cl.setWorkingDirectory(cwd);

			final StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();
			final StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

			final int result = CommandLineUtils.executeCommandLine(cl, stdout, stderr);
			
			log.debug("Output:\n" + stdout.getOutput() + '\n' + stderr.getOutput());

			if (result != EXIT_SUCCESS) {
				throw new MojoExecutionException("Error executing git checkout: " + stderr.getOutput());
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to execute git checkout: " + e.getMessage(), e);
		}
	}
}
