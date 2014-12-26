package com.nerdwin15.stash.webhook.service.eligibility;

import com.nerdwin15.stash.webhook.Notifier;
import com.nerdwin15.stash.webhook.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.stash.event.pull.PullRequestRescopedEvent;

/**
 * An EligibilityFilter that ensures PullRequestRescopedEvents come from
 * the "from" side.
 *
 * @author Michael Irwin (mikesir87)
 * @author Melvyn de Kort (lordmatanza)
 */
public class PullRequestRescopeEligibilityFilter implements EligibilityFilter {

  private static final Logger logger = // CHECKSTYLE:logger
      LoggerFactory.getLogger(PullRequestRescopeEligibilityFilter.class);

  private SettingsService settingsService;

  public PullRequestRescopeEligibilityFilter(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

    /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldDeliverNotification(EventContext context) {
    if (!PullRequestRescopedEvent.class
        .isAssignableFrom(context.getEventSource().getClass()))
      return true;

    if (!settingsService.getSettings(context.getRepository()).getBoolean(Notifier.TRIGGER_FOR_PULL_REQUEST_RESCOPED_FROM) && !settingsService.getSettings(context.getRepository()).getBoolean(Notifier.TRIGGER_FOR_PULL_REQUEST_RESCOPED_TO))
      return false;

    PullRequestRescopedEvent event = 
        (PullRequestRescopedEvent) context.getEventSource();
    if (settingsService.getSettings(context.getRepository()).getBoolean(Notifier.TRIGGER_FOR_PULL_REQUEST_RESCOPED_FROM) && !settingsService.getSettings(context.getRepository()).getBoolean(Notifier.TRIGGER_FOR_PULL_REQUEST_RESCOPED_TO)) {
        if (event.getPreviousFromHash().equals(event.getPullRequest().getFromRef().getLatestChangeset())) {
            logger.debug("Ignoring push event due to push not coming from the from-side");
            return false;
        }
    }
    if (settingsService.getSettings(context.getRepository()).getBoolean(Notifier.TRIGGER_FOR_PULL_REQUEST_RESCOPED_TO) && !settingsService.getSettings(context.getRepository()).getBoolean(Notifier.TRIGGER_FOR_PULL_REQUEST_RESCOPED_FROM)) {
        if (event.getPreviousToHash().equals(event.getPullRequest().getToRef().getLatestChangeset())) {
            logger.debug("Ignoring push event due to push not coming from the to-side");
              return false;
        }
    }
    
    return true;
  }
  
}
