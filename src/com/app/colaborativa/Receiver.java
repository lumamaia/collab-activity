package com.app.colaborativa;

import utils.PullParse;

import com.app.colaborativa.atividade.Feed;
import com.parse.ParsePushBroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends ParsePushBroadcastReceiver {

	@Override
	public void onPushOpen(Context context, Intent intent) {
		PullParse.setIsNotificacao(true);
		Intent i = new Intent(context, CollabActivityProjectActivity.class);
		i.putExtras(intent.getExtras());
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}
