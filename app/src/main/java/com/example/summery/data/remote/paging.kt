package com.example.summery.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.summery.data.OrderRepository
import com.example.summery.data.ProductRepository
import com.example.summery.data.remote.dto.OrderResponseDTO
import com.example.summery.network.ProductResponseDTO
import com.example.summery.uipages.ProductFilters

//P: this is the recommended thing for paging by google
//for massive scrolling espcially huh ..

//orders only for now
//so it's an abstract class, implements the function LOAD
class OrdersPagingSource(
    private val orderRepo: OrderRepository,
    //it needs it here ???
    private val sortingOption: String
): PagingSource<Int, OrderResponseDTO>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderResponseDTO>{
        val page = params.key ?: 0 // 0 start uhm


        val result = orderRepo.getMyOrders(
            page = page,
            size=params.loadSize,
            sortingOption = sortingOption
        )

        return if  (result.isSuccess){
            val pageResponse = result.getOrNull() ?: return LoadResult.Error(Exception("Failed to fetch page"))

            LoadResult.Page(
                data=pageResponse.content,
                prevKey = if(pageResponse.first) null else pageResponse.number -1,
                nextKey = if(pageResponse.last ||pageResponse.empty) null else page +1
            )

        }else {
            //val errorMessage  = "Failed to fetch this page"
            val exception = result.exceptionOrNull()
                ?: Exception("Unknown error on orders page $page")
            return LoadResult.Error(exception)
            //but my spring returns error messages parsed by OkHTTP..hmm

        }

        //CAN USE FOLD
        //return result.fold ( onSuccess = { ..  , onFailure = {

    }

    override fun getRefreshKey(state: PagingState<Int, OrderResponseDTO>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    //idk what black magic is this
    //TODO EXPLAINNN

    //-> PagingSource = CONTRACT with 2 types -> Int = type of the PAGE KEY (0,1,2)
    //OrderResponseDTO = type of the items being fetched (can it be generic ??)

    //params.key is null at first, make it 0 easy enough elvis

    //LOAD RESULT ??
    //-> = SEALED CLASS (enum that holds data we saw this )
    //give it: data + PAGE BEFORE THIS DATA (null fallback)
    //+ PAGE AFTER THIS DATA or null fallback
    //AUTOMATICALLY saves and updates the keys based on the scrolling

}

//SAME FOR PRODUCTS !! super clean super modern
class ProductsPagingSource(
    private val productRepo: ProductRepository,
    //sorting: -> category + price range + branch + rating
    private val filters: ProductFilters
): PagingSource<Int, ProductResponseDTO>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductResponseDTO>{
        val page = params.key ?: 0

        val result = productRepo.getProducts(
            query = filters.query,
            category = filters.category,
            minPrice = filters.minPrice,
            maxPrice = filters.maxPrice,
            minNote = filters.minRating,
            sellerId = filters.branchId,
            promo = filters.discounted,
            size = params.loadSize,
            page = page
        )

        return if(result.isSuccess){
            val pageResponse = result.getOrNull() ?: return LoadResult.Error(Exception("Failed to fetch products 0.0"))

            LoadResult.Page(
                data=pageResponse.content,
                prevKey = if(pageResponse.first) null else pageResponse.number -1,
                nextKey = if(pageResponse.last ||pageResponse.empty) null else page +1
            )
        }else{
            val exception = result.exceptionOrNull()
                ?: Exception("Unknown error on products page $page")
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductResponseDTO>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}