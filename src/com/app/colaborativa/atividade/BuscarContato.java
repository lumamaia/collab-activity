package com.app.colaborativa.atividade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import utils.Mask;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.app.colaborativa.CollabActivityProjectActivity;
import com.parse.ParseObject;
import com.app.colaborativa.R;

public class BuscarContato extends Activity {

	private EditText nome;
	private EditText prazo;
	private Date data_prazo;
	private Button salvar;
	private ImageButton voltar;
	public String contato_selecionado;

	public EditText busca_contato;
	public ArrayAdapter<String> adapterContato;
	String phoneNumber;
	ListView listTelefone;
	ArrayList<String> contato = new ArrayList<String>();

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buscar_contato);

		listTelefone = (ListView) findViewById(R.id.lista_contato);
		getNumber(this.getContentResolver());

		busca_contato = (EditText) findViewById(R.id.pesquisar_contato);
		
		busca_contato.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				BuscarContato.this.adapterContato.getFilter().filter(s);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		} );

		
		listTelefone.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long l) {
				contato_selecionado = adapterContato.getItem(position);
				Intent intent = new Intent(BuscarContato.this, NovoProjeto.class);
				intent.putExtra("contato", contato_selecionado);
				startActivity(intent);
			}
		});
			

		salvar = (Button) findViewById(R.id.bt_cancelar);
		salvar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent VoltarParaNovoProjeto = new Intent(BuscarContato.this,
						NovoProjeto.class);
				BuscarContato.this.startActivity(VoltarParaNovoProjeto);
				BuscarContato.this.finish();

			}
		});

	}


	public void getNumber(ContentResolver cr) {
		Cursor phones = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (phones.moveToNext()) {
			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			contato.add(name+" - "+phoneNumber);
		}
		phones.close();// close cursor
		adapterContato = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, contato);
		listTelefone.setAdapter(adapterContato);
		// display contact numbers in the list
	}

}
