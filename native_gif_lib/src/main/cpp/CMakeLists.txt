cmake_minimum_required(VERSION 3.10.2)

project("native_gif_lib")

set(SOURCE)

file(GLOB SOURCE ${CMAKE_SOURCE_DIR}/*.cpp ${CMAKE_SOURCE_DIR}/glflib/*.c)

add_library( 
             native_gif_lib
             SHARED
            ${SOURCE} )

set(LIBS)

list(APPEND LIBS log jnigraphics)

target_link_libraries( 
                       native_gif_lib
                       ${LIBS} )