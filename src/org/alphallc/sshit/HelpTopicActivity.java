/*
 * sshIt: simple, powerful, open-source SSH client for Android
 * Copyright 2014 Alpha LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alphallc.sshit;

import org.alphallc.sshit.util.HelpTopicView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Kenny Root
 *
 */
public class HelpTopicActivity extends Activity {
	public final static String TAG = "sshIt.HelpActivity";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.act_help_topic);

		String topic = getIntent().getStringExtra(Intent.EXTRA_TITLE);

		this.setTitle(String.format("%s: %s - %s",
				getResources().getText(R.string.app_name),
				getResources().getText(R.string.title_help),
				topic));

		HelpTopicView helpTopic = (HelpTopicView) findViewById(R.id.topic_text);

		helpTopic.setTopic(topic);
	}
}
