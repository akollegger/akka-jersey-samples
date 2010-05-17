echo "Testing User URLs..."
echo "get user"; curl http://localhost:9998/users/akollegger
echo "create user"; echo "TBD"
echo "update user"; curl -X PUT http://localhost:9998/users/akollegger
echo "delete user"; curl -X DELETE http://localhost:9998/users/akollegger

echo "Testing Bookmarks URLs..."
echo "get user bookmarks list"; curl http://localhost:9998/users/akollegger/books
echo "create bookmark"; echo "TBD"
echo "get bookmark"; curl http://localhost:9998/users/akollegger/books/hashcode
echo "update bookmark"; curl -X PUT http://localhost:9998/users/akollegger/books/hashcode
echo "delete bookmark"; curl -X DELETE http://localhost:9998/users/akollegger/books/hashcode

echo "Testing Tags URLs..."
echo "get user tags list"; curl http://localhost:9998/users/akollegger/tags
echo "get tag"; curl http://localhost:9998/users/akollegger/tags/tagname
echo "update bookmark"; curl -X PUT http://localhost:9998/users/akollegger/tags/tagname

echo "Testing Calendar URLs..."
echo "get user calendar history"; curl http://localhost:9998/users/akollegger/calendar
echo "get tag filtered history"; curl http://localhost:9998/users/akollegger/calendar/tagname

