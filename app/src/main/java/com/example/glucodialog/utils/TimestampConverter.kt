//package com.example.glucodialog.utils
//
//import com.example.glucodialog.ui.Timestamped
//import java.util.Date
//
//fun <T> List<T>.asTimestamped(getTimestamp: (T) -> Long): List<Timestamped<T>> {
//    return this.map { item ->
//        Timestamped(item, Date(getTimestamp(item)))
//    }
//}