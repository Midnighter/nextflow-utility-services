
class Custom {

    static Object doMap(channel) {
        def multi = channel.multiMap {
            a: it
            b: it
        }
        return multi.a
    }

}
