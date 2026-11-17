package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("ДОСЛІД 1: Узагальнення (Generics) проти Object");
        System.out.println("==================================================");
        runExperiment1();

        System.out.println("\n==================================================");
        System.out.println("ДОСЛІД 2: Продуктивність колекцій та видалення");
        System.out.println("==================================================");
        runExperiment2();

        System.out.println("\n==================================================");
        System.out.println("ДОСЛІД 3: Контракти equals та hashCode у HashSet");
        System.out.println("==================================================");
        runExperiment3();
    }

    private static void runExperiment1() {
        GenericBox<Title> box = new GenericBox<>();
        box.put(new Title(1, "Пуститися берега", 2008));


        Title validTitle = box.get();
        System.out.println("Успішно прочитано з GenericBox: " + validTitle);

        ObjectBox objBox = new ObjectBox();
        objBox.put("abc");

        System.out.println("Спроба прочитати Title з ObjectBox, де лежить String:");
        try {
            Title t = (Title) objBox.get();
            System.out.println(t);
        } catch (ClassCastException e) {
            e.printStackTrace(System.out);
        }

        List<String> genres = Arrays.asList("Драма", "Трилер", "Sci-Fi");
        printAnyList(genres);

        List<Integer> ints = Arrays.asList(10, 20, 30);
        List<Double> doubles = Arrays.asList(1.5, 2.5, 3.5);


        System.out.println("Сума List<Integer> через List<? extends Number>: " + totalFlexible(ints));
        System.out.println("Сума List<Double> через List<? extends Number>: " + totalFlexible(doubles));
    }

    public static <T> void printAnyList(List<T> list) {
        System.out.print("Елементи списку (" + list.size() + " шт.): ");
        for (T element : list) {
            System.out.print(element + " ");
        }
        System.out.println();
    }

    public static double totalStrict(List<Number> numbers) {
        double sum = 0;
        for (Number n : numbers) {
            sum += n.doubleValue();
        }
        return sum;
    }

    public static double totalFlexible(List<? extends Number> numbers) {
        double sum = 0;
        for (Number n : numbers) {
            sum += n.doubleValue();
        }
        return sum;
    }

    private static void runExperiment2() {
        int baseSize = 100_000;
        int operationsCount = 1_000;

        for (int run = 1; run <= 3; run++) {
            System.out.println("\n--- Замір №" + run + " (База: 100 000 елементів, операцій: 1 000) ---");

            List<Title> arrayList = new ArrayList<>(baseSize);
            List<Title> linkedList = new LinkedList<>();
            Set<Title> hashSet = new HashSet<>(baseSize);

            for (int i = 0; i < baseSize; i++) {
                Title t = new Title(i, "Тайтл #" + i, 2000 + (i % 25));
                arrayList.add(t);
                linkedList.add(t);
                hashSet.add(t);
            }

            long t0 = System.nanoTime();
            for (int i = 0; i < operationsCount; i++) {
                int middle = arrayList.size() / 2;
                arrayList.add(middle, new Title(baseSize + i, "Новий тайтл", 2026));
            }
            long arrayInsertMs = (System.nanoTime() - t0) / 1_000_000;

            t0 = System.nanoTime();
            for (int i = 0; i < operationsCount; i++) {
                int middle = linkedList.size() / 2;
                linkedList.add(middle, new Title(baseSize + i, "Новий тайтл", 2026));
            }
            long linkedInsertMs = (System.nanoTime() - t0) / 1_000_000;

            t0 = System.nanoTime();
            for (int i = 0; i < operationsCount; i++) {
                Title target = new Title(baseSize - 1 - i, "Пошук", 2020);
                arrayList.contains(target);
            }
            long arrayContainsMs = (System.nanoTime() - t0) / 1_000_000;

            t0 = System.nanoTime();
            for (int i = 0; i < operationsCount; i++) {
                Title target = new Title(baseSize - 1 - i, "Пошук", 2020);
                linkedList.contains(target);
            }
            long linkedContainsMs = (System.nanoTime() - t0) / 1_000_000;

            t0 = System.nanoTime();
            for (int i = 0; i < operationsCount; i++) {
                Title target = new Title(baseSize - 1 - i, "Пошук", 2020);
                hashSet.contains(target);
            }
            long hashContainsMs = (System.nanoTime() - t0) / 1_000_000;

            System.out.println("ArrayList  -> insert x1000: " + arrayInsertMs + " мс | contains x1000: " + arrayContainsMs + " мс");
            System.out.println("LinkedList -> insert x1000: " + linkedInsertMs + " мс | contains x1000: " + linkedContainsMs + " мс");
            System.out.println("HashSet    -> insert: не застосовується | contains x1000: " + hashContainsMs + " мс");
        }

        List<Title> titles = new ArrayList<>();
        titles.add(new Title(1, "Дивні дива", 2016));
        titles.add(new Title(2, "Друзі", 1994));
        titles.add(new Title(3, "Гра в кальмара", 2021));
        titles.add(new Title(4, "Клан Сопрано", 1999));

        System.out.println("\nСпроба видалити старі тайтли (рік < 2000) прямо в циклі for-each:");
        try {
            for (Title t : titles) {
                if (t.getYear() < 2000) {
                    titles.remove(t);
                }
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace(System.out);
        }

        System.out.println("\nПравильне видалення тих самих елементів через Iterator:");
        Iterator<Title> iterator = titles.iterator();
        while (iterator.hasNext()) {
            Title t = iterator.next();
            if (t.getYear() < 2000) {
                iterator.remove();
            }
        }
        System.out.println("Список після безпечного видалення через Iterator: " + titles);
    }

    private static void runExperiment3() {
        Set<TitleNoMethods> set1 = new HashSet<>();
        set1.add(new TitleNoMethods(101, "Чорнобиль", 2019));
        set1.add(new TitleNoMethods(101, "Чорнобиль (дублікат)", 2019));
        boolean contains1 = set1.contains(new TitleNoMethods(101, "Чорнобиль", 2019));
        System.out.println("1. Без equals і hashCode:     size = " + set1.size() + ", contains = " + contains1);

        Set<TitleOnlyEquals> set2 = new HashSet<>();
        set2.add(new TitleOnlyEquals(101, "Чорнобиль", 2019));
        set2.add(new TitleOnlyEquals(101, "Чорнобиль (дублікат)", 2019));
        boolean contains2 = set2.contains(new TitleOnlyEquals(101, "Чорнобиль", 2019));
        System.out.println("2. Тільки equals:             size = " + set2.size() + ", contains = " + contains2);

        Set<Title> set3 = new HashSet<>();
        Title firstTitle = new Title(101, "Чорнобиль", 2019);
        Title duplicateTitle = new Title(101, "Чорнобиль (дублікат)", 2019);
        set3.add(firstTitle);
        set3.add(duplicateTitle);
        boolean contains3 = set3.contains(new Title(101, "Чорнобиль", 2019));
        System.out.println("3. З equals та hashCode:      size = " + set3.size() + ", contains = " + contains3);

        System.out.println("\n--- Мутація поля id у об'єкта всередині HashSet ---");
        System.out.println("До зміни id: set3.contains(firstTitle) = " + set3.contains(firstTitle));

        firstTitle.setId(999);

        System.out.println("Змінили id з 101 на 999...");
        System.out.println("Пошук за новим станом set3.contains(firstTitle): " + set3.contains(firstTitle));
        System.out.println("Пошук за старим id=101: " + set3.contains(new Title(101, "Чорнобиль", 2019)));
        System.out.println("При цьому розмір множини set3.size() = " + set3.size() + " (об'єкт загубився у старому бакеті!)");
    }
}