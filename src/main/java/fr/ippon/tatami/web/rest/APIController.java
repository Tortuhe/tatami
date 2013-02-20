package fr.ippon.tatami.web.rest;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.ippon.tatami.domain.Group;
import fr.ippon.tatami.domain.Status;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.CounterRepository;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.MentionlineRepository;
import fr.ippon.tatami.repository.StatusRepository;
import fr.ippon.tatami.repository.TimelineRepository;
import fr.ippon.tatami.security.AuthenticationService;
import fr.ippon.tatami.service.GroupService;
import fr.ippon.tatami.service.SearchService;
import fr.ippon.tatami.service.StatusUpdateService;
import fr.ippon.tatami.service.TimelineService;
import fr.ippon.tatami.service.UserService;
import fr.ippon.tatami.service.dto.StatusDTO;

/**
 * REST controller for managing users.
 *
 * @author Kevin Yven, Pierre Monsimier, Benoit Lemaitre
 */
@Controller
public class APIController {

	private final Log log = LogFactory.getLog(APIController.class);

	@Inject
	private UserService userService;

	@Inject
	private AuthenticationService authenticationService;

	@Inject
	Environment env;

	@Inject
	private StatusRepository statusRepository;

	@Inject
	private GroupService groupService;
	
	@Inject
	private StatusUpdateService statusUpdateService;
	
	@Inject
	private TimelineService timelineService;
	
	@Inject MentionlineRepository mentionlineRepository;


	@Inject
	private TimelineRepository timelineRepository;
	
	@Inject
	private FollowerRepository followerRepository;

	@Inject
	private CounterRepository counterRepository;

	@Inject
	private SearchService searchService;

	@RequestMapping(value = "/API/new", method = RequestMethod.POST)
	@ResponseBody
	public void newTweet(@RequestParam(required = true) String user, @RequestParam(required = true) String apiKey ,@RequestParam(required = true) String content) {

		if (this.log.isDebugEnabled()) {
			this.log.debug("API request : new tweet");
		}

		User u = userService.getUserByLogin(user);

		if(u != null && u.getApiKey().equals(apiKey))
		{
			String escapedContent = StringEscapeUtils.escapeHtml(content);
			statusUpdateService.postStatus(u.getLogin(), escapedContent, false, null);	
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug("User not found or apiKey not correct");
				this.log.debug("User : " + ((u != null) ? u.getLogin() : ""));
				this.log.debug("Api key attendue : " + ((u != null) ? u.getApiKey() : ""));
				
			}
		}

	}
	
	@RequestMapping(value = "/API/get",
			method = RequestMethod.GET,
            produces = "application/json")
	@ResponseBody
	public Collection<StatusDTO> getTweet(@RequestParam(required = true) String user, @RequestParam(required = true) String apiKey) {

		if (this.log.isDebugEnabled()) {
			this.log.debug("API request : getStatus");
		}

		User u = userService.getUserByLogin(user);

		if(u != null && u.getApiKey().equals(apiKey))
		{
			return timelineService.getUserTimeline(u.getLogin(), 100, null, null);
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug("User not found or apiKey not correct");
				this.log.debug("User : " + ((u != null) ? u.getLogin() : ""));
				this.log.debug("Api key attendue : " + ((u != null) ? u.getApiKey() : ""));
				
			}
		}
		return null;
	}
}
