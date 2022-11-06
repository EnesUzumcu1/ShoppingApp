<h1 align="center"> ShoppingApp </h1>

<p align="middle">
  <img src="https://user-images.githubusercontent.com/61823965/200175656-05eca4e7-7a4a-4a19-a4d9-4affed0bc5d2.png" width="200">
  <img src="https://user-images.githubusercontent.com/61823965/200175649-e7385b7a-9a31-4377-92b4-83b100d7d806.png" width="200">
  <img src="https://user-images.githubusercontent.com/61823965/200175654-c78d695f-0d8f-4443-9294-95ce2918a7cb.png" width="200">
  <img src="https://user-images.githubusercontent.com/61823965/200175655-9e3d316c-38df-4082-a82d-8ce6002fce6b.png" width="200">
  <img src="https://user-images.githubusercontent.com/61823965/200175647-2d5799b0-18d8-41f0-9cf9-37fd5502155e.png" width="200">
  <img src="https://user-images.githubusercontent.com/61823965/200175646-1e5c08e1-02bb-4ed7-9957-f852109ab831.png" width="200">
  <img src="https://user-images.githubusercontent.com/61823965/200175642-cbaf99e8-a829-4f54-a414-d6527e58623e.png" width="200">
  <img src="https://user-images.githubusercontent.com/61823965/200175651-37f6b101-6d5b-4d6e-8df2-8799c030df54.png" width="200">
  <img src="https://user-images.githubusercontent.com/61823965/200175650-eecdf512-7402-4b5a-803d-2598ce127c0c.png" width="200">
</p>

## Table of Contents

* [About the Project](#about-the-project)
  * [Architecture](#architecture)
* [Features](#features)

## About The Project

ShoppingApp is an online shopping application. FakeStoreAPI was used to get the product list and categories. You can subscribe to the application using the Firebase E-mail service and view the product list. You can create your own basket.

## Architecture
MVVM (Model-View-ViewModel) architecture pattern and data binding is used in the development of this application. The development language of the application is Kotlin.

* Architecture;
    * [Data Binding](https://developer.android.com/topic/libraries/data-binding/)
    * [View Binding](https://developer.android.com/topic/libraries/view-binding)
    * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
    * [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) 
    * [Navigation](https://developer.android.com/guide/navigation)
    * [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
    
* Third parties;
    * [Kotlin Coroutines (Kotlin flows)](https://developer.android.com/kotlin/flow)
    * [Flow](https://developer.android.com/kotlin/flow)
    * [Gson](https://github.com/google/gson)
    * [Retrofit](https://github.com/square/retrofit)
    * [Glide](https://bumptech.github.io/glide/) 
    * [Firebase](https://firebase.google.com)
    * [Firebase Auth](https://firebase.google.com/docs/auth)
    * [Lottie](https://github.com/airbnb/lottie-android)
    
## Features
   - Users can sign up by email and create their own basket.
   - The users can view all products.
   - The users can view selected product details. On the detail page, the users can add the product to their basket.
   - The user can increase or decrease the products in the basket.
   - The users can search by product name or category on the search page.
   - When products are added to the basket, the total price is updated.
   - The user can view their information on the profile page.

