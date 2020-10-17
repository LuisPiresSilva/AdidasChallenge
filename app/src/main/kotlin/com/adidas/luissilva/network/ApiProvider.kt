package com.adidas.luissilva.network

import javax.inject.Inject

/**
 * The Api provider, this class is responsible to manage all the connections with clients' api and user session.
 */
class ApiProvider @Inject constructor(private val service: EndpointCollection) {

    fun goalsSync() = service.goalsSync()

}
