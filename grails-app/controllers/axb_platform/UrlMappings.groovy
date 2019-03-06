package axb_platform

class UrlMappings {

    static mappings = {
        delete "/$controller/$id(.$format)?"(action:"delete")
        get "/$controller(.$format)?"(action:"index")
        get "/$controller/$id(.$format)?"(action:"show")
        post "/$controller(.$format)?"(action:"save")
        put "/$controller/$id(.$format)?"(action:"update")
        patch "/$controller/$id(.$format)?"(action:"patch")

        post "/$controller/massUpdate(.$format)?"(action:"massUpdate")
        post "/$controller/massTransfer(.$format)?"(action:"massTransfer")
        post "/$controller/massUpload(.$format)?"(action:"massUpload")
        post "/$controller/massDelete(.$format)?"(action:"massDelete")

        "/"(controller: 'application', action:'index')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
