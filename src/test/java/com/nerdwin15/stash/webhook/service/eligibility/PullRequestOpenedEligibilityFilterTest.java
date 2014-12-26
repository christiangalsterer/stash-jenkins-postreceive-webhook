package com.nerdwin15.stash.webhook.service.eligibility;

import com.atlassian.stash.event.pull.PullRequestOpenedEvent;
import com.atlassian.stash.event.pull.PullRequestRescopedEvent;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.setting.Settings;
import com.nerdwin15.stash.webhook.Notifier;
import com.nerdwin15.stash.webhook.service.SettingsService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test case for the {@link com.nerdwin15.stash.webhook.service.eligibility.PullRequestOpenedEligibilityFilter} class
 *
 * @author Christian Galsterer (christiangalsterer)
 */
public class PullRequestOpenedEligibilityFilterTest {

  private PullRequestOpenedEligibilityFilter filter;
  private SettingsService settingsService;
  private Repository repo;
  private EventContext eventContext;
  private PullRequestOpenedEvent openEvent;
  private Settings settings;
  
  /**
   * Setup tasks
   */
  @Before
  public void setUp() throws Exception {
    repo = mock(Repository.class);
    settings = mock(Settings.class);
    settingsService = mock(SettingsService.class);

    filter = new PullRequestOpenedEligibilityFilter(settingsService);
    
    openEvent = mock(PullRequestOpenedEvent.class);
    eventContext = mock(EventContext.class);
    when(eventContext.getEventSource()).thenReturn(openEvent);
    when(eventContext.getRepository()).thenReturn(repo);
  }

  /**
   * Validate that when the event source is not a PullRequestOpenedEvent, the filter just passes it on.
   */
  @Test
  public void shouldIgnoreIfEventSourceNotPullRequestOpenedEvent() {
    PullRequestRescopedEvent event = mock(PullRequestRescopedEvent.class);
    when(eventContext.getEventSource()).thenReturn(event);
    when(settingsService.getSettings(repo)).thenReturn(settings);

    assertTrue(filter.shouldDeliverNotification(eventContext));
  }

  /**
   * Validate that when the event source is not a PullRequestReopenedEvent, the filter just passes it on.
   */
  @Test
  public void shouldIgnoreIfEventSourceNotPullRequestReopenedEvent() {
    PullRequestRescopedEvent event = mock(PullRequestRescopedEvent.class);
    when(eventContext.getEventSource()).thenReturn(event);
    when(settingsService.getSettings(repo)).thenReturn(settings);

    assertTrue(filter.shouldDeliverNotification(eventContext));
  }

  /**
   * Validate that when the support for opened or reopened PR is not activated don't notify Jenkins.
   */
  @Test
  public void shouldNotContinueIfSupportForPullRequestNotActivated() {
    when(settingsService.getSettings(repo)).thenReturn(settings);
    when(settings.getBoolean(Notifier.TRIGGER_FOR_PULL_REQUEST_OPENED)).thenReturn(false);

    assertFalse(filter.shouldDeliverNotification(eventContext));
  }

}
