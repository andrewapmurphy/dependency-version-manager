package com.cyndre.dvm.server.uri;

import org.springframework.web.servlet.view.RedirectView;

public class LocalRedirectView extends RedirectView {
	private static final boolean FORCE_CONTEXT_RELATIVE = true;

	public LocalRedirectView() {
		super();
		setContextRelative(FORCE_CONTEXT_RELATIVE);
	}

	public LocalRedirectView(String url, boolean contextRelative,
			boolean http10Compatible, boolean exposeModelAttributes) {
		super(url, FORCE_CONTEXT_RELATIVE, http10Compatible, exposeModelAttributes);
	}

	public LocalRedirectView(String url, boolean contextRelative,
			boolean http10Compatible) {
		super(url, FORCE_CONTEXT_RELATIVE, http10Compatible);
	}

	public LocalRedirectView(String url, boolean contextRelative) {
		super(url, FORCE_CONTEXT_RELATIVE);
	}

	public LocalRedirectView(String url) {
		super(url);
		setContextRelative(FORCE_CONTEXT_RELATIVE);
	}
	
}
