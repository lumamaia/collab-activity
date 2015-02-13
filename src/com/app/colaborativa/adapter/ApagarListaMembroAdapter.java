package com.app.colaborativa.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.app.colaborativa.R;

public class ApagarListaMembroAdapter extends ArrayAdapter<ParseUser> implements
		Filterable {

	private Context context;
	private static List<ParseUser> membros = null;
	private static List<ParseUser> membrosList = null;
	private static List<ParseUser> original = null;
	private Filter filter;

	public ApagarListaMembroAdapter(Context context, List<ParseUser> membros) {
		super(context, 0, membros);
		this.context = context;
		this.original = new ArrayList<ParseUser>(membros);
        this.membros = new ArrayList<ParseUser>(membros);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ParseObject membro = membros.get(position);

		if (view == null)
			view = LayoutInflater.from(context).inflate(R.layout.lista_membro,
					null);

		TextView textViewNome = (TextView) view.findViewById(R.id.membro_nome);
		textViewNome.setText(membro.getString("nome"));

		TextView textViewTelefone = (TextView) view
				.findViewById(R.id.membro_telefone);
		textViewTelefone.setText(membro.getString("celular"));

		return view;
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new MembroFilter();
		}
		return filter;
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private class MembroFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<ParseUser> filteredItems = new ArrayList<ParseUser>();

				for (int i = 0, l = original.size(); i < l; i++) {
					ParseUser membro = original.get(i);
					if (membro.getString("nome").toLowerCase()
							.contains(constraint))
						filteredItems.add(membro);
				}
				result.count = filteredItems.size();
				result.values = filteredItems;
			} else {
				synchronized (this) {
					result.values = original;
					result.count = original.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			membros = (ArrayList<ParseUser>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = membros.size(); i < l; i++) 
				add(membros.get(i));
			notifyDataSetInvalidated();
		}
	}

}