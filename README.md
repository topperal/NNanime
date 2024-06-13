# NNanime
## Нейросетевые технологии и методы математической статистики для прогнозирования рейтингов анимационных произведений
\
В данном резпозитории находится программная реализация математической модели для прогнозирования рейтингов японской мултипликации (аниме) по метаданным, известных до выхода произведения.

Основные составляющие:

- NNanime
- android_app
- rawdata - набор данных, содержащий произведения с 2000 по 2023 год включительно

## NNanime

Приложение на языке программирования Java, которое создает наборы данных и реализует математическую модель для предсказания рейтингов.

Содержит следующий функционал:
- Создание датасета (Класс GetInfo)
- Оценка качества модели (Класс FinalFormul)
- Формирование и отправление json-объекта, содержащего спрогнозированный значения (Классы ProcessingManga, ProcessingGenre, ProcessingStudio, ProcessingTheme, ProcessingRating и ProcessingDemographic)  

## android_app

Мобильное приложение на базе ОС Android, осуществляющее взаимодействие с пользователем и вывод результирующего прогноза рейтинга.

Содержит следующие фрагменты:
- PredictFrag нужен для ввода и запоминания рейтинга манги
- StudioFrag вместе с адаптером StudioAdapter дает возможность найти по поиску студию и выбрать её, одновременно запоминая название, дисперсию и её рейтинг
- GenreFrag в паре с адаптером GenreAdapter позволяет найти по поиску и выбрать один или несколько жанров, одновременно запоминая название, дисперсию и рейтинг выбранных параметров.
- AdditionalParametrs нужен для выбора дополнительных параметров: темы, возрастного ограничения и демографии. 
- ResultFrag используется для конечного расчета формулы и её вывода.

Содержит файл nnanime.apk, который позволяет установить приложение на телефон.

## Основные результаты

Был разработан и реализован алгоритм, позволяющий получать данные о произведениях за настраиваемые интервалы времени. Также была реализована математическая модель, которая предсказывает значения параметров с помощью архитектуры MLP, метода наименьших квадратов и дисперсии. Android приложение выступает в качестве графического интерфейса для отображения результирующих данных пользователю. 
\
RMSE модели на выборке за 2023 год составляет 0.3. Для спрогнозированных рейтингов за зиму 2024 RMSE = 0.4. 
