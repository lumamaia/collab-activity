package utils;

import java.util.List;

import android.os.AsyncTask;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DeleteAll extends AsyncTask<ParseObject, Void, Void>{

	@Override
	protected Void doInBackground(ParseObject... params) {
		deleteAtividade(params[0]);
		return null;
	}

	
	public void deleteAtividade(ParseObject atividade) {

		deleteComentarios(atividade);
		deleteFeeds(atividade);
		deleteConvites(atividade);
		atividade.deleteInBackground();
		
	}
	
	public void deleteComentarios(ParseObject atividade) {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("comentario");
		query.whereEqualTo("atividade", atividade);

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {

				for (ParseObject object : objects) {
					object.deleteInBackground();
				}

			}
		});
	}
	
	public void deleteFeedsProjeto(ParseObject projeto) {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("feed");
		query.whereEqualTo("projeto", projeto);

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {

				for (ParseObject object : objects) {
					object.deleteInBackground();
				}

			}
		});
	}

	public void deleteFeeds(ParseObject atividade) {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("feed");
		query.whereEqualTo("atividade", atividade);

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {

				for (ParseObject object : objects) {
					object.deleteInBackground();
				}

			}
		});
	}
	
	
	public void deleteConvites(ParseObject atividade) {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("convite_responsavel");
		query.whereEqualTo("atividade", atividade);

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {

				for (ParseObject object : objects) {
					object.deleteInBackground();
				}

			}
		});
	}


	public void deleteProjeto(ParseObject projeto) {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("atividade");
		query.whereEqualTo("projeto", projeto);

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {

				for (ParseObject object : objects) {
					deleteAtividade(object);
				}

			}
		});
		
		deleteFeedsProjeto(projeto);
		projeto.deleteInBackground();
		
	}







	
}
