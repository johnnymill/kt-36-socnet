# Отличия объекта Notes от оригинала

Описание оригинального объекта взято [здесь](https://dev.vk.com/method/notes)

### Метод getFriendsNotes()

Не реализован совсем, т.к. они утверждают, что "Данный метод устарел
и может быть отключён через некоторое время, пожалуйста, избегайте его
использования."

### Метод add()

Добавлены аргументы:
* ownerId

Проигнорированы аргументы:
* privacy
* comment_privacy
* privacy_view
* privacy_comment

### Метод createComment()

Добавлены аргументы:
* fromId

Проигнорированы аргументы:
* reply_to
* guid

### Метод delete()

Добавлены аргументы:
* ownerId

### Метод deleteComment()

Добавлены аргументы:
* unrecoverable

### Метод edit()

Добавлены аргументы:
* ownerId

Проигнорированы аргументы:
* privacy
* comment_privacy
* privacy_view
* privacy_comment

### Метод editComment()

Нет отличий

### Метод get()

Добавлены аргументы:
* ownerId (просто переименованный user_id)

Проигнорированы аргументы:
* offset
* count
* sort

### Метод getById()

Проигнорированы аргументы:
* need_wiki

### Метод getComments()

Проигнорированы аргументы:
* sort
* offset
* count

### Метод restoreComment()

Нет отличий
