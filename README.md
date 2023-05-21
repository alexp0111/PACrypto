# PACrypto
## Описание

### Приложение ориентировано на получение актуальной информации о курсе криптовалют и просмотра исторических данных о курсах
### Реализует следующий функционал:
* Поиск валюты по названию и тикеру
* Просмотр информации о курсе валюты в виде графика на выбранном отрезке времени
* Просмотр OHLCV (Open, High, Low, Close, Volume)
* Добавление инструмента в закладки
* Возможность подписки на на валюту с указанием времени дня и дней недели
* Переход по QR-коду на страницу валюты как из приложения, так и при помощи внешнего приложения

### О программной составляющей
* Язык разработки: Kotlin
* В качестве архитектурного паттерна используется MVVM, как функционально достаточная и актуальная система
* Для работы с API используется библиотека Retrofit
* Для работы с локальной БД используется Google Room
* Для реализации offline-first логики используется паттерн networkBoundResource представленный в офф. репозитории android в раздлеле architecture-components-samples
* В качестве data-binding инструмента используется coroutineFlow
* Для реализации уведомлений используется комбинация coroutineWorker и notificationChannel

### Доп. информация
Приложение меняет тему в зависимости от системной, а также имеет локализацию на английском и русском языках

## Скриншоты
<img src="https://github.com/alexp0111/PACrypto/assets/62151474/f23faf40-4858-48e2-93ae-2e76138acecb" width="250"/>
<img src="https://github.com/alexp0111/PACrypto/assets/62151474/16611a1f-42da-4f9d-ab17-3560a86556a6" width="250"/>
<img src="https://github.com/alexp0111/PACrypto/assets/62151474/451e55a7-4691-48a8-926a-2e80e9bf17e2" width="250"/>
<img src="https://github.com/alexp0111/PACrypto/assets/62151474/2b475ac1-629b-4aee-bd17-16cc6e836aa2" width="250"/>
<img src="https://github.com/alexp0111/PACrypto/assets/62151474/f769cd5f-a164-4911-9c77-6943c7413fbf" width="250"/>
<img src="https://github.com/alexp0111/PACrypto/assets/62151474/41941bc9-f4d4-46e1-9185-7c25c0c32b7f" width="250"/>

## QR-коды для тестов
* Bitcoin
<img src="https://github.com/alexp0111/PACrypto/assets/62151474/0558c074-b2d0-4474-a090-5393e1eaa004" width="250"/>

* Etherium
<img src="https://github.com/alexp0111/PACrypto/assets/62151474/646d6bb5-507a-42d6-ad61-1f33ed66fa23" width="250"/>

## Требования к устройству для запуска
* MIN SDK: 26
* Gradle JDK: 11
* Наличие камеры у устройства
* Разрешение на отправку уведомлений
