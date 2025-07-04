package com.app.k2t.di


import android.content.Context
import com.app.k2t.cloudinary.CloudinaryManager
import com.app.k2t.firebase.auth.FirebaseAuthManager
import com.app.k2t.firebase.repository.OrderItemRepository
import com.app.k2t.firebase.repositoryimpl.FoodCategoryRepositoryImpl
import com.app.k2t.firebase.repositoryimpl.FoodRepositoryImpl
import com.app.k2t.firebase.repositoryimpl.OrderItemRepositoryImpl
import com.app.k2t.firebase.repositoryimpl.OrderRepositoryImpl
import com.app.k2t.firebase.repositoryimpl.UserRepositoryImpl
import com.app.k2t.local.database.CartDatabase
import com.app.k2t.local.repository.CartRepository
import com.app.k2t.ui.presentation.viewmodel.CartViewModel
import com.app.k2t.ui.presentation.viewmodel.FoodCategoryViewModel
import com.app.k2t.ui.presentation.viewmodel.FoodViewModel
import com.app.k2t.ui.presentation.viewmodel.OrderItemViewModel
import com.app.k2t.ui.presentation.viewmodel.OrderViewModel
import com.app.k2t.ui.presentation.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel


val  appModule = module {
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<FirebaseFirestore> { FirebaseFirestore.getInstance() }

    // Database remains the same
    single { CartDatabase.getDatabase(androidContext()) }
    single { get<CartDatabase>().cartDao() }


    // repositories
    single { FoodRepositoryImpl(get()) }
    single { FoodCategoryRepositoryImpl() }
    single { CartRepository(get()) }
    single { UserRepositoryImpl(get()) }
    single { FirebaseAuthManager(get(),get())  }
    single { OrderItemRepositoryImpl(get()) }
    single { OrderRepositoryImpl(get()) }
    single { CloudinaryManager(androidContext()) }


//    viewModel
    viewModel { FoodViewModel() }
    viewModel { FoodCategoryViewModel() }
    viewModel { CartViewModel()  }
    viewModel { UserViewModel() }
    viewModel { OrderViewModel() }
    viewModel { OrderItemViewModel() }



}