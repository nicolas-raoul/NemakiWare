package controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import constant.Token;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

	@Override
	public String getUsername(Context ctx) {
		final String requestRepoId = extractRepositoryId(ctx.request());
		final String sessionRepoId = ctx.session().get(Token.LOGIN_REPOSITORY_ID);
		if (StringUtils.isBlank(sessionRepoId) || sessionRepoId.equals(requestRepoId)) {
			return ctx.session().get(Token.LOGIN_USER_ID);
		}

		// Redirect to login page
		return null;
	}

	@Override
	public Result onUnauthorized(Context ctx) {
		String sessionRepoId = ctx.session().get(Token.LOGIN_REPOSITORY_ID);
		if(StringUtils.isBlank(sessionRepoId)){
			final String requestRepoId = extractRepositoryId(ctx.request());
			if(StringUtils.isBlank(requestRepoId)){
				return redirect(routes.Application.error()); 
			}else{
				return redirect(routes.Application.login(requestRepoId));
			}
		}else{
			return redirect(routes.Application.login(sessionRepoId));
		}
	}

	private String extractRepositoryId(Request request) {
		String uri = request.uri();
		Pattern p = Pattern.compile("^/ui/repo/([^/]*).*");
		Matcher m = p.matcher(uri);
		if (m.find()) {
			return m.group(1);
		}
		
		return null;
	}
}
