import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zstan on 01.12.16.
 */
public class MapCache {
/*
    // добавить в оба метода нотаций : synchronized, если это стороняя библиотека и сделать невозможно - писать свои обертки
    public byte[] loadFromFile(String filename) {};
    public void saveToFile(String filename, byte[] data) {};


    private final long MEM_LIMIT = 100_000_000; // ну почти 100 )
    private AtomicLong id = new AtomicLong(0);
    private volatile long storageSize = 0;

    ConcurrentHashMap<Long, byte[]> storage = new ConcurrentHashMap<>();

    // если перестаем умещаться в мапу - кладем только ключи, значения в файловый сторадж
    // может не самый поптимальный вариант, но если подумать можно еще подужаться = хранить не в мапе.
    // тут три варианта
    // 1. можем чуть промахнуться с лимитом памяти, но жить с быстрой ConcurrentMap, тогда :
    public long putToCache1(byte[] data) {
        // Preconditions ... if data != Null ...
        long currId = id.getAndIncrement();
        if (storageSize + data.length > MEM_LIMIT) {
            saveToFile(...);
            storage.put(currId, null);
        } else {
            storage.put(currId, data);
        }
        storageSize += data.length; // промахнемся по памяти, потому что можем уже положить НО еще не обновить размер
        return currId;
    }

    // 2. тут теряется красота в скорости работы с concurrentMap и вообще тут уже нужна обычная мапа, синхронизируемся секцией
    public long putToCache2(byte[] data) {
        // Preconditions ... if data != Null ...
        long currId = id.getAndIncrement();
        synchronized (this) {
            if (storageSize + data.length > MEM_LIMIT) {
                saveToFile(...);
                storage.put(currId, null);
            } else {
                storage.put(currId, data);
            }
            storageSize += data.length;
        }
        return currId;
    }

    // 3. по сути ускоренный метод 1.
    public long putToCache3(byte[] data) {
        // Preconditions ... if data != Null ...
        long currId = id.getAndIncrement();
        if (storageSize + data.length > MEM_LIMIT) {

            // ExecutorService ex = Executors.newCachedThreadPool(); где то выше
            ex.submit(() -> {saveToFile(...);})
            storage.put(currId, null);
        } else {
            storage.put(currId, data);
        }
        storageSize += data.length; // тоже можно промахнуться по памяти как и в первом случае, но тут уже все побыстрее.
        return currId;
    }
*/

    //public byte[] getFromCache(int id) {
    //    if (storage.get())
        /* и вот тут у нас проблемы с реализацией персистентных функций, если бы они возвращали позицию записи на диск,
        можно было бы хранить всю эту информацию и хоть и медленнее мапы, но все же ходить в диск (еще лучше с вытеснением),
        опять же - если это сторонняя либа - можно написать обертки, но тут проблема с loadFromFile, ее придется тоже куда то положить в памяти,
        ну или ее часть ...
        Если же реализовывать через текущую имплементацию тут каждый раз придется загружать в память с диска ну и опять же - где то хранить (счас не реализовано)
        смещение и размер.
        Но лучше, конечно поменять имплементацию персистентных функций :)
        */
    //}

}
