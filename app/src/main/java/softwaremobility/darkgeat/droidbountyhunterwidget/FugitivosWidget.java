package softwaremobility.darkgeat.droidbountyhunterwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Calendar;

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
    public static final String ACTION = "training.giovas.droidbountyhunter.ACTUALIZAR_WIDGET";

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
            controles.setTextViewText(R.id.labelNombre,nombre);
            if(capturado.equals("0")){
                controles.setTextViewText(R.id.labelFugitivoCapturado,"Fugitivos");
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
                controles.setImageViewBitmap(R.id.imgFoto,bitmap);
            }else if(path.equals("")){
                controles.setTextViewText(R.id.labelFugitivoCapturado,"Capturados");
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
                controles.setImageViewBitmap(R.id.imgFoto,bitmap);
            }else{
                controles.setTextViewText(R.id.labelFugitivoCapturado,"Capturados");
                Bitmap bitmap = PictureTools.decodeSampledBitmapFromUri(path,50,50);
                controles.setImageViewBitmap(R.id.imgFoto,bitmap);
            }
        }catch (Exception e){

        }
        Intent intent = new Intent(ACTION);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,widgetId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        controles.setOnClickPendingIntent(R.id.btn_cambiar,pendingIntent);
        appWidgetManager.updateAppWidget(widgetId,controles);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(ACTION)){
            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

            capturado = (capturado == "0") ? "1" : "0";

            if(widgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                actualizarWidget(context,widgetManager,widgetId);
            }
        }
        if(CLOCK_WIDGET_UPDATE.equals(intent.getAction())){
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(),getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetId : ids){
                actualizarWidget(context,appWidgetManager,appWidgetId);
            }
        }
    }

    private PendingIntent createClockTickIntent(Context context){
        Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);
        alarmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),15000,createClockTickIntent(context));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
    }
}
