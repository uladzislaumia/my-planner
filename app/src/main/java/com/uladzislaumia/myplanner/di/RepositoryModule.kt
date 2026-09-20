package com.uladzislaumia.myplanner.di

import com.uladzislaumia.myplanner.data.repository.FirebaseAuthRepositoryImpl
import com.uladzislaumia.myplanner.data.repository.RoomPlannerRepositoryImpl
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: FirebaseAuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPlannerRepository(
        plannerRepositoryImpl: RoomPlannerRepositoryImpl
    ): PlannerRepository
}
