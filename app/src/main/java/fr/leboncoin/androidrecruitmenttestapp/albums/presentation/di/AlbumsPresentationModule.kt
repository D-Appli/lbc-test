package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.di

import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.mapper.AlbumsScreenMapper
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.mapper.AlbumsScreenMapperImpl
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer.AlbumsScreenReducer
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer.AlbumsScreenReducerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AlbumsPresentationModule {

    @Binds
    fun provideAlbumsScreenReducer(reducer: AlbumsScreenReducerImpl): AlbumsScreenReducer

    @Binds
    fun provideAlbumsScreenMapper(mapper: AlbumsScreenMapperImpl): AlbumsScreenMapper
}
