package ru.i_novus.platform.datastorage.temporal.service;

import ru.i_novus.platform.datastorage.temporal.model.DataDifference;
import ru.i_novus.platform.datastorage.temporal.model.MetaDifference;

import java.util.Date;

/**
 * Сервис сравнения наборов данных и структур данных в хранилищах.
 * Набор данных внутри заданного хранилища - это набор записей, актуальных на заданную дату.
 * Структура данных в пределах одного хранилища неизменна, но может быть разной в разных хранилищах.
 *
 * @author lgalimova
 * @since 31.01.2018
 */
public interface CompareDataService {
    /**
     * Сравнение актуальных на заданные даты данных
     *
     * @param storageCode    код хранилища данных
     * @param baseDataDate   дата актуальности для исходных данных
     * @param targetDataDate дата актуальности для целевого набора данных
     * @return Результат сравнения
     */
    DataDifference getDataDifference(String storageCode, Date baseDataDate, Date targetDataDate);

    /**
     * Сравненние структур данных в двух хранилищах
     *
     * @param baseStorageCode   код хранилища с исходной структурой данных
     * @param targetStorageCode код хранилища с целевой структурой данных
     * @return Результат сравнения
     */
    MetaDifference getMetaDifference(String baseStorageCode, String targetStorageCode);
}