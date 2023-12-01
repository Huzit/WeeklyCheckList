package com.weekly.weeklychecklist.util

open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    //참조객체
    private var _creator:((A) -> T)? = creator
    //휘발성
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T{
        //인스턴스가 생성되있는지 확인
        val checkInstance = instance
        if(checkInstance != null)
            return checkInstance
        //없을 시 단일스레드만 접근 가능
        return synchronized(this){
            val checkInstanceAgain = instance
            if(checkInstanceAgain != null)
                return checkInstanceAgain
            else{
                //객체 참조로 넘어온 인스턴스 저장
                val created = _creator!!(arg)
                instance = created
                //비우고
                _creator = null
                //리턴
                created
            }
        }
    }
}

open class SingletonHolderNoProperty<out T>(creator: () -> T){
    private var _creator: (() -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(): T{
        val checkInstance = instance
        if(checkInstance != null)
            return checkInstance
        return synchronized(this){
            val checkInstanceAgain = instance
            if(checkInstanceAgain != null)
                return checkInstanceAgain
            else {
                val created = _creator!!()
                instance = created
                _creator = null
                created
            }
        }
    }
}