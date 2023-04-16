package com.example.mediaplayer.data.datacontroller

class DataController {
    val foo = FavoriteObjectOperations()
    val mdo = MetaDataOperations(foo)
    val dso = DataSetOperations()
}