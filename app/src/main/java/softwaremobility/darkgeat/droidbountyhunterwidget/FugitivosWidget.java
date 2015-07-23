package softwaremobility.darkgeat.droidbountyhunterwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Created by darkgeat on 23/07/15.
 */
public class FugitivosWidget extends AppWidgetProvider {
    public static final String uri = "content://training.giovas.droidbountyhunter";
    public static final Uri CONTENT_URI = Uri.parse(uri);
    public static final String column_name = "name";
    public static final String column_foto = "foto";

    public static String capturado = "0";
    public static String CLOCK_WIDGET_UPDATE = "training.giovas.droidbountyhunter.ACTUALIZAR_SEG_WIDGET";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int i = 0 ; i < appWidgetIds.length ; i++){
            int widgetId = appWidgetIds[i];
            actualizarWidget(context,appWidgetManager,widgetId);
        }
    }

    private static void actualizarWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        RemoteViews controles = new RemoteViews(context.getPackageName(),R.layout.widget_principal);

        try{
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(CONTENT_URI, null, capturado, null, null);
            String nombre = "", path = "";
            if (cur.moveToFirst()){
                nombre = cur.getString(cur.getColumnIndex(column_name));
                path = cur.getString(cur.getColumnIndex(column_foto));
            }
            cur.close();
            controles.setTextViewText(R.id.labelFugitivoCapturado,nombre);
            if(capturado.equals("0")){
                
            }
        }catch (Exception e){

        }
    }
}
