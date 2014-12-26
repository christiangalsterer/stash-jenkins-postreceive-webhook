package com.nerdwin15.stash.webhook.service.eligibility;

import com.atlassian.stash.event.pull.PullRequestOpenedEvent;
import com.atlassian.stash.event.pull.PullRequestReopenedEvent;
import com.nerdwin15.stash.webhook.Notifier;
import com.nerdwin15.stash.webhook.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An EligibilityFilter that ensures that notifications are enabled for opened or reopened pull requests.
 *
 * @author Christian Galsterer (christiangalsterer)
 */
public class PullRequestOpenedEligibilityFilter implements EligibilityFilter {

  private static final Logger logger = // CHECKSTYLE:logger
      LoggerFactory.getLogger(PullRequestOpenedEligibilityFilter.class);

  private SettingsService settingsService;

  public PullRequestOpenedEligibilityFilter(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldDeliverNotification(EventContext context) {
    if (!PullRequestOpenedEvent.class.isAssignableFrom(context.getEventSource().getClass()) && !PullRequestReopenedEvent.class
            .isAssignableFrom(context.getEventSource().getClass()))
      return true;

    return settingsService.getSettings(context.getRepository()).getBoolean(Notifier.TRIGGER_FOR_PULL_REQUEST_OPENED);
  }
  
}
