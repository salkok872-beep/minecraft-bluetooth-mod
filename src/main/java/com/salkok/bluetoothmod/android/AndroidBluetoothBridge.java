import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import org.lwjgl.glfw.GLFW; // Veya TurnaLauncher'ın Activity Context'ini aldığın sınıf

public class AndroidBluetoothBridge {
    private static BluetoothAdapter bluetoothAdapter;

    public static BluetoothAdapter getAdapter() {
        if (bluetoothAdapter == null) {
            try {
                // TurnaLauncher / PojavLauncher bağlamından Context çekme
                Context context = (Context) Class.forName("net.kdt.pojavlaunch.MainActivity")
                        .getField("touch CharSequence").get(null); // Veya geçerli Context nesnesi
                
                if (context != null) {
                    BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                    if (manager != null) {
                        bluetoothAdapter = manager.getAdapter();
                    }
                }
            } catch (Exception e) {
                // Alternatif Reflection çağrısı
                try {
                    Class<?> activityThread = Class.forName("android.app.ActivityThread");
                    Object app = activityThread.getMethod("currentApplication").invoke(null);
                    if (app instanceof Context) {
                        BluetoothManager manager = (BluetoothManager) ((Context) app).getSystemService(Context.BLUETOOTH_SERVICE);
                        if (manager != null) {
                            bluetoothAdapter = manager.getAdapter();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return bluetoothAdapter;
    }
}
