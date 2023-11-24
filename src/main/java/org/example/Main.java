package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.client.HttpClients;

import java.io.FileOutputStream;
import java.io.IOException;

//import static org.apache.http.config.RequestConfig.*;

public class Main {
  public static void main(String[] args) throws IOException {

    String url = "https://api.nasa.gov/planetary/" +
        "apod?api_key=" +
        "Lg0c43bxc62szxpoXWwYz9b3LC8CCMA35UKkdHFT" +
        "&date=2023-11-18";  // можем подставить любую дату, если ее убрать то загрузится текущая

    // создаем переменную
//    CloseableHttpClient httpClient = HttpClients.createDefault();
    CloseableHttpClient httpClient = HttpClientBuilder.create()
        .setDefaultRequestConfig(RequestConfig.custom()
            .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
            .setSocketTimeout(30000)    // максимальное время ожидания получения данных
            .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
            .build())
        .build();
//    CloseableHttpClient httpClient = HttpClientBuilder.create()
//        .setDefaultRequestConfig(custom()
//            .setConnectTimeout(Timeout.ofMilliseconds(5000))    // максимальное время ожидание подключения к серверу
//            .setSocketTimeout(30000)    // максимальное время ожидания получения данных
//            .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
//            .build())
//        .build();

    // создаем запрос
    CloseableHttpResponse response = httpClient.execute(new HttpGet(url));

    /*
     Создаем в папке src/ файл answer.json и вручную копируем в него ответ из терминала
     форматируем и смотрим что можно с ним сделать
     гуглим чем и как парсить JSON на Java, выбираем Jackson
     находим его на репозитории Maven -> https://mvnrepository.com -> jackson databind
     выбираем версию, берем оттуда код погрузки библиотек вставляем в файл pom.xls
     внутрь тега <dependencies> перезагружаем Maven
    */

    // Создаем ObjectMapper
    ObjectMapper mapper = new ObjectMapper();

    // coздаем новый файл Java class -> NasaAnswer

    NasaAnswer answer = mapper.readValue(response.getEntity().getContent(), NasaAnswer.class);
//    System.out.println(answer.title);
//    System.out.println(answer.url);

    // Создаем переменную в которую заносим имя файла в который будем сохранять данные
//    String fileName = "image.jpg"
//     Добавляем текущую дату к имени файла
//    String fileName = "image" + LocalDate.now() + ".jpg";
    // Берем имя файла из ответа сервера
    String[] urlSeparated = answer.url.split("/");
    String fileName = urlSeparated[urlSeparated.length - 1];

    // создаем запрос в объект image скачается картинка из ответа сервера answer.url
    CloseableHttpResponse image = httpClient.execute(new HttpGet(answer.url));
//    CloseableHttpResponse image = httpClient.execute(new HttpGet(answer.hdurl));

    // FileOutputStream - поток вывода файла (подготавливаем файл fileName для записи)
    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
    // получаем доступ к контенту getEntity() и записываем данные в файл
    image.getEntity().writeTo(fileOutputStream);
    // закрываем файл
    fileOutputStream.close();
  }

}