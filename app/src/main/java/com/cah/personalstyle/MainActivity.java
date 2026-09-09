package com.cah.personalstyle;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    private static final int PICK_ME = 10, PICK_CLOTHES = 11;
    private LinearLayout body, closetGrid;
    private android.content.SharedPreferences prefs;
    private final int wine = Color.rgb(122, 53, 83), ink = Color.rgb(43, 32, 39);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b); prefs = getSharedPreferences("style", MODE_PRIVATE); showHome();
    }

    private TextView text(String value, int size, boolean title) {
        TextView v = new TextView(this); v.setText(value); v.setTextSize(size); v.setTextColor(ink);
        v.setPadding(8, title ? 18 : 8, 8, 12); if (title) v.setTypeface(null, 1); return v;
    }
    private Button button(String label, View.OnClickListener action) {
        Button b = new Button(this); b.setText(label); b.setTextColor(Color.WHITE); b.setBackgroundColor(wine);
        b.setAllCaps(false); b.setOnClickListener(action); b.setPadding(12, 12, 12, 12);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2); p.setMargins(0, 8, 0, 8); b.setLayoutParams(p); return b;
    }
    private void shell(String title) {
        ScrollView scroll = new ScrollView(this); scroll.setBackgroundColor(Color.rgb(250,246,247));
        body = new LinearLayout(this); body.setOrientation(LinearLayout.VERTICAL); body.setPadding(32, 38, 32, 40);
        body.addView(text(title, 28, true)); scroll.addView(body); setContentView(scroll);
    }
    private void showHome() {
        shell("Meu Personal Style ✦");
        body.addView(text("Seu closet inteligente, com a sua personalidade.", 16, false));
        String profile = prefs.getString("profile", "Faça o teste para descobrir seu DNA de estilo.");
        body.addView(text(profile, 17, true));
        body.addView(button("Fazer meu teste de estilo", v -> showQuiz()));
        body.addView(button("Minha foto-base", v -> pick(PICK_ME, false)));
        body.addView(button("Adicionar roupas ao closet", v -> pick(PICK_CLOTHES, true)));
        body.addView(button("Abrir meu closet", v -> showCloset()));
        body.addView(button("Montar look para hoje", v -> suggestLook()));
        body.addView(text("Sua foto e suas roupas permanecem no aparelho. A prova virtual por IA será ativada somente por conexão segura.", 12, false));
    }
    private void pick(int code, boolean multiple) {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("image/*"); i.addCategory(Intent.CATEGORY_OPENABLE);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple); startActivityForResult(i, code);
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data); if (result != RESULT_OK || data == null) return;
        if (request == PICK_ME && data.getData() != null) saveUri("me", data.getData());
        if (request == PICK_CLOTHES) {
            Set<String> set = new LinkedHashSet<>(prefs.getStringSet("clothes", new LinkedHashSet<>()));
            if (data.getClipData() != null) for(int x=0;x<data.getClipData().getItemCount();x++) { Uri u=data.getClipData().getItemAt(x).getUri(); persist(u); set.add(u.toString()); }
            else if (data.getData()!=null) { persist(data.getData()); set.add(data.getData().toString()); }
            prefs.edit().putStringSet("clothes", set).apply();
        }
        showHome();
    }
    private void saveUri(String key, Uri u) { persist(u); prefs.edit().putString(key, u.toString()).apply(); }
    private void persist(Uri u) { try { getContentResolver().takePersistableUriPermission(u, Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch(Exception ignored){} }

    private Spinner spinner(String[] values) { Spinner s=new Spinner(this); ArrayAdapter<String>a=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,values);s.setAdapter(a);body.addView(s);return s; }
    private void showQuiz() {
        shell("Seu DNA de estilo");
        body.addView(text("Seu signo", 16, true)); Spinner sign=spinner(new String[]{"Leão","Áries","Touro","Gêmeos","Câncer","Virgem","Libra","Escorpião","Sagitário","Capricórnio","Aquário","Peixes"});
        body.addView(text("Sua cartela de cores", 16, true)); Spinner color=spinner(new String[]{"Ainda não sei","Inverno","Verão","Outono","Primavera"});
        body.addView(text("Sua rotina profissional", 16, true)); Spinner job=spinner(new String[]{"Médica / consultório","Executiva","Criativa","Casual","Eventos e palco"});
        body.addView(text("Estilo que mais combina com você", 16, true)); Spinner style=spinner(new String[]{"Elegante contemporâneo","Romântico","Clássico","Dramático","Sexy sofisticado","Criativo","Natural"});
        body.addView(button("Salvar meu perfil", v -> {
            String result="Leitura pessoal: "+sign.getSelectedItem()+" • "+color.getSelectedItem()+" • "+job.getSelectedItem()+" • "+style.getSelectedItem();
            prefs.edit().putString("profile",result).putString("styleName",style.getSelectedItem().toString()).apply(); showHome();
        }));
        body.addView(button("Voltar", v -> showHome()));
    }
    private void showCloset() {
        shell("Meu closet");
        Set<String> set=prefs.getStringSet("clothes", Collections.emptySet());
        if(set.isEmpty()) body.addView(text("Seu closet ainda está vazio. Adicione fotos das suas peças.",16,false));
        for(String item:set){ ImageView image=new ImageView(this); image.setImageURI(Uri.parse(item)); image.setScaleType(ImageView.ScaleType.CENTER_CROP); body.addView(image,new LinearLayout.LayoutParams(-1,420)); }
        body.addView(button("Adicionar mais roupas", v -> pick(PICK_CLOTHES,true)));
        body.addView(button("Voltar", v -> showHome()));
    }
    private void suggestLook() {
        Set<String> set=prefs.getStringSet("clothes", Collections.emptySet());
        if(set.size()<2){ new AlertDialog.Builder(this).setTitle("Vamos completar seu closet").setMessage("Adicione pelo menos duas peças para eu montar uma combinação.").setPositiveButton("Adicionar",(d,w)->pick(PICK_CLOTHES,true)).setNegativeButton("Depois",null).show();return; }
        shell("Look sugerido ✦");
        List<String> list=new ArrayList<>(set); Collections.shuffle(list);
        body.addView(text("Pensado para: "+prefs.getString("styleName","seu estilo pessoal"),16,false));
        for(int i=0;i<Math.min(3,list.size());i++){ImageView image=new ImageView(this);image.setImageURI(Uri.parse(list.get(i)));image.setScaleType(ImageView.ScaleType.CENTER_CROP);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,360);p.setMargins(0,8,0,8);body.addView(image,p);}
        body.addView(text("Dica: repita uma cor em dois pontos do look e deixe a terceira peça criar contraste.",15,true));
        body.addView(button("Gerar outra combinação",v->suggestLook())); body.addView(button("Voltar",v->showHome()));
    }
}
